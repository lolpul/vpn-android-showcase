package example.state

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

// A platform-neutral state holder that can be owned by an Android ViewModel.
// All actions and callbacks must use the same single-thread UI dispatcher.
class ConnectionModel(
    private val repository: ConnectionRepository,
    private val hint: CachedHint,
    dispatcher: CoroutineDispatcher,
    parent: Job? = null,
    private val nowMillis: () -> Long = System::currentTimeMillis,
) {
    private val lifetime = SupervisorJob(parent)
    private val scope = CoroutineScope(lifetime + dispatcher)
    private val mutableState = MutableStateFlow<ConnectionState>(ConnectionState.Loading)
    val state: StateFlow<ConnectionState> = mutableState.asStateFlow()
    private var operation: Job? = null
    private var revision = 0L

    fun reconcile() {
        replaceOperation(ConnectionState.Loading) {
            val preparation = repository.lookupPreparation()
            if (preparation != null && preparation.validUntilMillis > nowMillis()) {
                ConnectionState.Ready
            } else {
                ConnectionState.Disconnected
            }
        }
    }

    fun connect(): Boolean {
        if (state.value != ConnectionState.Ready || !lifetime.isActive) return false
        replaceOperation(ConnectionState.Connecting) {
            repository.begin()
            ConnectionState.Connected
        }
        return true
    }

    fun cancelCurrent() {
        if (!lifetime.isActive) return
        revision++
        operation?.cancel()
        // This means the UI operation was cancelled, not that a real VPN stopped.
        mutableState.value = ConnectionState.Cancelled
    }

    private fun replaceOperation(
        pending: ConnectionState,
        read: suspend () -> ConnectionState,
    ) {
        if (!lifetime.isActive) return
        val requestRevision = ++revision
        operation?.cancel()
        mutableState.value = pending
        operation = scope.launch {
            try {
                val next = read()
                // A superseded or cancelled result cannot overwrite newer intent,
                // even when a faulty dependency returns after cancellation.
                if (requestRevision == revision && isActive) {
                    mutableState.value = next
                    hint.prepared = next == ConnectionState.Ready || next == ConnectionState.Connected
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: RepositoryUnavailable) {
                if (requestRevision == revision && isActive) {
                    mutableState.value = ConnectionState.Unavailable
                }
            }
        }
    }

    // Call from the owner's lifecycle, never from one of this model's workers.
    // Joining establishes worker cleanup; merely cancelling does not.
    suspend fun shutdown() {
        revision++
        lifetime.cancelAndJoin()
        mutableState.value = ConnectionState.Closed
    }
}
