package task

import java.util.*

const val TAG = "TASK: "

open class BasicTask(
    override val priority: Priority = Priority.LOW,
    override val name: String = UUID.randomUUID().toString(),
    override val executionTime: Long = 1000,
    override var suspendingTime: Long = 1000
) : Task {

    override var state: State = State.SUSPENDED
    override var isDone: Boolean = false

    override fun run() {
        start()

        try {
            println(TAG + "Start work: $this")
            Thread.sleep(executionTime)
            println(TAG + "End work: $this")
            isDone = true
        } catch (e: InterruptedException) {
            println(TAG + "Thread interrupted while sleeping")
            return
        }

        terminate() // when we shutdownNow this is executed, before sleep ends - this is not problem, just not terminate at scheduler - but its wrong behaviour!
    }

    override fun decreaseSuspendingTime() {
        suspendingTime -= 1
        if (suspendingTime <= 0) {
            activate()
        }
    }

    // Новая задача переводится в состояние готовности
    override fun activate() {
        if (state == State.SUSPENDED) {
            state = State.READY
            println(TAG + "Task goes READY: $this")
        } else {
            throw Exception("Illegal state of the task $name: state was $state but must be SUSPENDED")
        }
    }

    // Выполняется задача, выбранная планировщиком
    override fun start() {
        if (state == State.READY) {
            state = State.RUNNING
            println(TAG + "Task goes RUNNING: $this")
        } else {
            throw Exception("Illegal state of the task $name: state was $state but must be READY")
        }
    }

    // Планировщик запускает другую задачу. Запущенная задача переводится в состояние готовности
    override fun preempt() {
        if (state == State.RUNNING) {
            state = State.READY
            println(TAG + "Task goes READY: $this")
        } else {
            throw Exception("Illegal state of the task $name: state was $state but must be RUNNING")
        }
    }

    // Запушенная задача переходит в состояние suspended
    override fun terminate() {
        if (state == State.RUNNING) {
            state = State.SUSPENDED
            println(TAG + "Task goes SUSPENDED: $this")
        } else {
            throw Exception("Illegal state of the task $name: state was $state but must be RUNNING")
        }
    }

    override fun toString() =
        "BasicTask(name=$name, state=$state, priority=$priority, executionTime=$executionTime, suspendingTime=$suspendingTime)"
}
