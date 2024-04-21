package task

import LogService
import java.util.*

open class BasicTask(
    override val priority: Priority = Priority.LOW,
    override val name: String = UUID.randomUUID().toString(),
    override val executionTime: Long = 100,
    override var suspendingTime: Long = 100
) : Task {

    private val logService = LogService()

    override var state: State = State.SUSPENDED

    override var postRunAction: (() -> Unit)? = null

    override fun run() {
        start()

        try {
            Thread.sleep(executionTime)
            isDone = true
        } catch (e: InterruptedException) {
            logService.processorThreadInterruption()
            return
        }

        terminate()
        postRunAction?.invoke()
    }

    override fun decreaseSuspendingTime() {
        suspendingTime -= 1
        if (suspendingTime <= 0) activate()
    }

    // Новая задача переводится в состояние готовности
    override fun activate() {
        if (state == State.SUSPENDED) {
            state = State.READY
            logService.taskChangedState(this, State.SUSPENDED, State.READY)
        } else {
            logService.taskErrorWhileChangingState(this, State.SUSPENDED, state)
            throw Exception("Illegal state of the task $name: state was $state but must be SUSPENDED")
        }
    }

    // Выполняется задача, выбранная планировщиком
    override fun start() {
        if (state == State.READY) {
            state = State.RUNNING
            logService.taskChangedState(this, State.READY, State.RUNNING)
        } else {
            logService.taskErrorWhileChangingState(this, State.READY, state)
            throw Exception("Illegal state of the task $name: state was $state but must be READY")
        }
    }

    // Планировщик запускает другую задачу. Запущенная задача переводится в состояние готовности
    override fun preempt() {
        if (state == State.RUNNING) {
            state = State.READY
            logService.taskChangedState(this, State.RUNNING, State.READY)
        } else {
            logService.taskErrorWhileChangingState(this, State.RUNNING, state)
            throw Exception("Illegal state of the task $name: state was $state but must be RUNNING")
        }
    }

    // Запушенная задача переходит в состояние suspended
    override fun terminate() {
        if (state == State.RUNNING) {
            state = State.SUSPENDED
            logService.taskChangedState(this, State.RUNNING, State.SUSPENDED)
        } else {
            logService.taskErrorWhileChangingState(this, State.RUNNING, state)
            throw Exception("Illegal state of the task $name: state was $state but must be RUNNING")
        }
    }

    override fun toString() =
        "BasicTask(name=$name, state=$state, priority=$priority, executionTime=$executionTime, suspendingTime=$suspendingTime)"
}
