package task

import java.util.UUID

class ExtendedTask(
    priority: Priority = Priority.LOW,
    name: String = UUID.randomUUID().toString(),
    executionTime: Long = 100,
    suspendingTime: Long = 100,
    var untilWaitTime: Long = -1,
) : BasicTask(priority, name, executionTime, suspendingTime) {

    var waitAction: (() -> Unit)? = null

    override fun run() {
        start()

        if (untilWaitTime > 0) {
            try {
                Thread.sleep(untilWaitTime)
            } catch (e: InterruptedException) {
                logService.processorThreadInterruption()
                return
            }
            await()
            waitAction?.invoke()
        } else {
            try {
                Thread.sleep(executionTime)
            } catch (e: InterruptedException) {
                logService.processorThreadInterruption()
                return
            }
            terminate()
            onTermination?.invoke()
        }
    }

    // Выполнение задачи продолжится только после выполнения события
    fun await() {
        if (state == State.RUNNING) {
            state = State.WAITING
            untilWaitTime = -1
            logService.taskChangedState(this, State.RUNNING, State.WAITING)
        } else {
            throw Exception("Illegal state of the task $name: state was $state but must be RUNNING")
        }
    }

    // Произошло по крайней мере одно событие, которое ожидала задача.
    fun release() {
        if (state == State.WAITING) {
            state = State.READY
            logService.taskChangedState(this, State.WAITING, State.READY)
        } else {
            throw Exception("Illegal state of the task $name: state was $state but must be WAITING")
        }
    }

    override fun toString() =
        "ExtendedTask(name=$name, state=$state, priority=$priority, executionTime=$executionTime, suspendingTime=$suspendingTime)"
}
