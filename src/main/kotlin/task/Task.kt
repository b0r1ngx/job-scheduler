package task

import statemachine.State

interface Task: Runnable {
    val name: String
    var state: State
    val priority: Priority
    val executionTime: Long
    var suspendingTime: Long

    fun activateSM()

    fun startSM()

    fun preemptSM()

    fun terminateSM()

    fun decreaseSuspendingTime()
}