package statemachine

sealed class State {
    data object Ready : State() {
        fun start() = Running
    }

    data object Waiting : State() {
        fun release() = Ready
    }

    data object Running : State() {
        fun preempt() = Ready
        fun terminate() = Suspended
        fun wait() = Waiting
    }

    data object Suspended : State() {
        fun activate() = Ready
    }
}

data class StateData(
    val state: State
)

fun StateData.nextState() {
//    this.copy(state = State.Suspended)
//    state = when (state) {
//        is State.Ready -> state.start()
//        is State.Running -> TODO()
//        is State.Suspended -> state.activate()
//        is State.Waiting -> state.release()
//    }
}