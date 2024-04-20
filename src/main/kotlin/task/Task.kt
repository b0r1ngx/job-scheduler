package task

interface Task : Runnable {

    val name: String
    var state: State
    val priority: Priority
    val executionTime: Long
    val suspendingTime: Long

    fun activate()

    fun start()

    fun preempt()

    fun terminate()
}
