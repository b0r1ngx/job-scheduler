package model.task

import java.util.UUID

class Task(
    priority: Priority,
    val name: String = UUID.randomUUID().toString(),
    val type: Type = Type.Basic
) : Runnable, Comparable<Task> {
    var _priority: Priority = priority
        private set

    private val timestamp = System.currentTimeMillis()

    override fun run() {
        Thread.sleep(1)
    }

    override fun toString(): String {
        return "Task(name=$name, type=$type, priority=$_priority)"
    }

    override fun compareTo(other: Task): Int {
        return (this.timestamp - other.timestamp).toInt()
    }
}