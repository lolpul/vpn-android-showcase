# Discussing state and reconciliation

Problem: an asynchronous response or cached record can mislead a screen about current connection state. Constraints: one UI dispatcher, a narrow repository contract, owned coroutines, and a sample without Android or VPN internals.

Decisions: sealed states make distinctions explicit; read-only StateFlow keeps UI observation separate from actions; authoritative lookup validates preparation/expiry; cached metadata is only a hint. A restored record is Ready, not Connected. Replacement operations cancel predecessors and reject late results. Shutdown joins jobs, while cancellation remains a request rather than proof of external disconnection.

Alternatives: independent booleans can represent contradictory combinations; a Mutex/actor can support callers from multiple threads but adds machinery not needed under UI confinement; events can preserve every transition but are different from StateFlow's latest-state semantics. An Android ViewModel integration would supply lifecycle ownership and rendering separately.

Failure modes: stale/expired hints, out-of-order completion, duplicate actions, expected repository outage, cancelled partial work, and callbacks arriving after shutdown. An uncooperative dependency can delay join. Unexpected defects are not mapped as normal repository failures. Host tests cannot validate device/service state.

Trade-offs: confinement is simple but must be obeyed; revision guards suppress stale UI writes but do not undo external side effects; cancellation must be honored by adapters; a fake hint does not simulate process-death persistence.

Questions: Why is Ready distinct from Connected? Why not trust a cached value? Why rethrow CancellationException? Can StateFlow omit intermediate states? What does the revision guard protect, and what does it not protect? Who owns the dispatcher/jobs? How would you attach this holder to ViewModel/Compose and reconcile an actual service after cancellation?
