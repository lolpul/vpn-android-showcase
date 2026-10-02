package example.state

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ConnectionModelTest {
    private class FakeRepository : ConnectionRepository {
        var lookup: suspend () -> Preparation? = { Preparation(200) }
        var start: suspend () -> Unit = {}
        var starts = 0

        override suspend fun lookupPreparation() = lookup()
        override suspend fun begin() {
            starts++
            start()
        }
    }

    @Test
    fun initialStateDoesNotTrustCachedHint() = runTest {
        val model = ConnectionModel(FakeRepository(), CachedHint(true), StandardTestDispatcher(testScheduler))
        assertEquals(ConnectionState.Loading, model.state.value)
        assertFalse(model.connect())
        model.shutdown()
    }

    @Test
    fun validPreparationIsReadyUntilExplicitOperationSucceeds() = runTest {
        val repository = FakeRepository()
        val model = ConnectionModel(repository, CachedHint(), StandardTestDispatcher(testScheduler), nowMillis = { 100 })
        model.reconcile()
        runCurrent()
        assertEquals(ConnectionState.Ready, model.state.value)
        assertEquals(0, repository.starts)
        assertTrue(model.connect())
        assertEquals(ConnectionState.Connecting, model.state.value)
        assertFalse(model.connect())
        runCurrent()
        assertEquals(ConnectionState.Connected, model.state.value)
        assertEquals(1, repository.starts)
        model.shutdown()
    }

    @Test
    fun absentOrExpiredPreparationClearsStaleHint() = runTest {
        for (snapshot in listOf(null, Preparation(99), Preparation(100))) {
            val repository = FakeRepository().apply { lookup = { snapshot } }
            val hint = CachedHint(true)
            val model = ConnectionModel(repository, hint, StandardTestDispatcher(testScheduler), nowMillis = { 100 })
            model.reconcile()
            runCurrent()
            assertEquals(ConnectionState.Disconnected, model.state.value)
            assertFalse(hint.prepared)
            model.shutdown()
        }
    }

    @Test
    fun lookupFailureIsUnavailableRatherThanCachedSuccess() = runTest {
        val repository = FakeRepository().apply { lookup = { throw RepositoryUnavailable() } }
        val model = ConnectionModel(repository, CachedHint(true), StandardTestDispatcher(testScheduler))
        model.reconcile()
        runCurrent()
        assertEquals(ConnectionState.Unavailable, model.state.value)
        model.shutdown()
    }

    @Test
    fun operationFailureHasAnExplicitState() = runTest {
        val repository = FakeRepository().apply { start = { throw RepositoryUnavailable() } }
        val model = ConnectionModel(repository, CachedHint(), StandardTestDispatcher(testScheduler), nowMillis = { 100 })
        model.reconcile()
        runCurrent()
        model.connect()
        runCurrent()
        assertEquals(ConnectionState.Unavailable, model.state.value)
        model.shutdown()
    }

    @Test
    fun cancelledOperationUnwindsAndDoesNotBecomeAnError() = runTest {
        var cleaned = false
        val repository = FakeRepository().apply {
            start = { try { awaitCancellation() } finally { cleaned = true } }
        }
        val model = ConnectionModel(repository, CachedHint(), StandardTestDispatcher(testScheduler), nowMillis = { 100 })
        model.reconcile()
        runCurrent()
        model.connect()
        runCurrent()
        model.cancelCurrent()
        runCurrent()
        assertTrue(cleaned)
        assertEquals(ConnectionState.Cancelled, model.state.value)
        model.shutdown()
    }

    @Test
    fun restartedModelRechecksAuthorityAndDoesNotRestoreConnected() = runTest {
        val repository = FakeRepository()
        val hint = CachedHint()
        val dispatcher = StandardTestDispatcher(testScheduler)
        val first = ConnectionModel(repository, hint, dispatcher, nowMillis = { 100 })
        first.reconcile()
        runCurrent()
        first.connect()
        runCurrent()
        first.shutdown()
        assertTrue(hint.prepared)
        val second = ConnectionModel(repository, hint, dispatcher, nowMillis = { 100 })
        assertEquals(ConnectionState.Loading, second.state.value)
        second.reconcile()
        runCurrent()
        assertEquals(ConnectionState.Ready, second.state.value)
        assertEquals(1, repository.starts)
        second.shutdown()
    }

    @Test
    fun observerSeesTheOperationOrdering() = runTest {
        val model = ConnectionModel(FakeRepository(), CachedHint(), StandardTestDispatcher(testScheduler), nowMillis = { 100 })
        val observed = mutableListOf<ConnectionState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.state.toList(observed) }
        model.reconcile()
        runCurrent()
        model.connect()
        runCurrent()
        assertEquals(listOf(ConnectionState.Loading, ConnectionState.Ready, ConnectionState.Connecting, ConnectionState.Connected), observed)
        model.shutdown()
    }

    @Test
    fun supersededLookupCannotOverwriteNewerIntent() = runTest {
        val oldResult = CompletableDeferred<Preparation?>()
        val repository = FakeRepository().apply { lookup = { withContext(NonCancellable) { oldResult.await() } } }
        val hint = CachedHint()
        val model = ConnectionModel(repository, hint, StandardTestDispatcher(testScheduler), nowMillis = { 100 })
        model.reconcile()
        runCurrent()
        repository.lookup = { Preparation(200) }
        model.reconcile()
        runCurrent()
        assertEquals(ConnectionState.Ready, model.state.value)
        oldResult.complete(null)
        runCurrent()
        assertEquals(ConnectionState.Ready, model.state.value)
        assertTrue(hint.prepared)
        model.shutdown()
    }

    @Test
    fun shutdownJoinsWorkersAndPreventsFurtherActions() = runTest {
        var cleaned = false
        val repository = FakeRepository().apply {
            lookup = { try { awaitCancellation() } finally { cleaned = true } }
        }
        val model = ConnectionModel(repository, CachedHint(), StandardTestDispatcher(testScheduler))
        model.reconcile()
        runCurrent()
        model.shutdown()
        assertTrue(cleaned)
        assertEquals(ConnectionState.Closed, model.state.value)
        model.reconcile()
        assertFalse(model.connect())
        model.cancelCurrent()
        assertEquals(ConnectionState.Closed, model.state.value)
        model.shutdown()
    }

    @Test
    fun parentLifecycleCancellationReachesOwnedWork() = runTest {
        val parent = Job()
        var cleaned = false
        val repository = FakeRepository().apply {
            lookup = { try { awaitCancellation() } finally { cleaned = true } }
        }
        val model = ConnectionModel(repository, CachedHint(), StandardTestDispatcher(testScheduler), parent)
        model.reconcile()
        runCurrent()
        parent.cancel()
        runCurrent()
        assertTrue(cleaned)
        assertFalse(model.connect())
        model.shutdown()
        assertEquals(ConnectionState.Closed, model.state.value)
    }
}
