package task

import statemachine.State
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

class Task(
    priority: Priority,
    val name: String = UUID.randomUUID().toString(),
    val type: Type = Type.Basic
) : Runnable {
    var _priority: Priority = priority
        private set

    private var state = State.Ready

    override fun run() {
        Thread.sleep(1)
        // after run process success -> became to next state?
    }

    override fun toString(): String {
        return "Task(name=$name, type=$type, priority=$_priority)"
    }
}