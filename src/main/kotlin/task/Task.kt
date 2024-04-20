package task

import statemachine.State
import java.util.UUID

// TODO: For Sergey, Bind State to Task, to States start live by lifecycle
class Task(
    val priority: Priority,
    val name: String = UUID.randomUUID().toString(),
    val type: Type = Type.Basic,
    val timeToExecute: Long = 1000,
    private var timeToTaskGoesFromSuspendedToReady: Int = 1000
) : Runnable {
    var state: State = State.Suspended
        private set

    override fun run() {
        Thread.sleep(timeToExecute)
        // after run process success -> became to next state?
    }

    fun decreaseTimeToTaskGoesFromSuspendedToReady() {
        timeToTaskGoesFromSuspendedToReady -= 1
    }

    fun isTaskReady() =
        timeToTaskGoesFromSuspendedToReady == 0


    override fun toString(): String {
        return "Task(name=$name, type=$type, priority=$priority)"
    }
}