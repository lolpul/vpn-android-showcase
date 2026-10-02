package example.state

sealed interface ConnectionState {
    data object Loading : ConnectionState
    data object Disconnected : ConnectionState
    data object Ready : ConnectionState
    data object Connecting : ConnectionState
    data object Connected : ConnectionState
    data object Cancelled : ConnectionState
    data object Unavailable : ConnectionState
    data object Closed : ConnectionState
}
