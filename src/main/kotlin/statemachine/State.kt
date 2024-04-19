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
