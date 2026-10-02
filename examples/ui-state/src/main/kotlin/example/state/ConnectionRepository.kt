package example.state

data class Preparation(val validUntilMillis: Long)

class RepositoryUnavailable : Exception()

interface ConnectionRepository {
    // An authoritative preparation record is not evidence of a running connection.
    suspend fun lookupPreparation(): Preparation?

    // The fake operation must cooperate with cancellation and unwind partial work.
    // Real adapters translate expected failures to RepositoryUnavailable.
    suspend fun begin()
}

// A stand-in for persisted metadata. It is never used as current UI authority.
// Access, like model actions, is confined to the UI dispatcher's thread.
class CachedHint(var prepared: Boolean = false)
