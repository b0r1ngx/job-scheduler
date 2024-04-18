package model.task

import java.util.UUID

class Task(
    priority: Priority,
    val type: Type = Type.Basic,
    val name: String = UUID.randomUUID().toString()
) : Runnable {
    var _priority: Priority = priority
        private set

    override fun run() {
        Thread.sleep(1)
    }

    override fun toString(): String {
        return "Task(name=$name, type=$type, priority=$_priority)"
    }
}