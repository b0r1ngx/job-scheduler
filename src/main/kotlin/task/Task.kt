package task

interface Task : Runnable {

    val name: String
    var state: State
    val priority: Priority
    val executionTime: Long
    var suspendingTime: Long
    var postRunAction: (() -> Unit)?

    fun activate()
    fun start()
    fun preempt()
    fun terminate()
    fun decreaseSuspendingTime()

}
