package model

class Task(
    priority: Priority,
    val name: String = ""
) : Runnable {
    var _priority: Priority = priority
        private set

    override fun run() {
        Thread.sleep(100)
    }

    override fun toString(): String {
        return "Task(priority=$_priority, name=$name)"
    }
}