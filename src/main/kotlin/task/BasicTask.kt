package task

import statemachine.State
import java.util.*

class BasicTask(
    val priority: Priority = Priority.LOW,
    val name: String = UUID.randomUUID().toString(),
    val executionTime: Long = 1000,
    private var suspendingTime: Int = 1000
) : Runnable {

    var state: State = State.SUSPENDED
        private set

    // Новая задача переводится в состояние готовности
    private fun activate() {
        if (state == State.SUSPENDED) {
            state = State.READY
        } else {
            throw Exception("Illegal state of the task $name: $state but must be SUSPENDED")
        }
    }

    // Выполняется задача, выбранная планировщиком
    fun start() {
        if (state == State.READY) {
            state = State.RUNNING
            run()
        } else {
            throw Exception("Illegal state of the task $name: $state but must be READY")
        }
    }

    // Планировщик запускает другую задачу. Запущенная задача переводится в состояние готовности
    fun preempt() {
        if (state == State.RUNNING) {
            state = State.READY
        } else {
            throw Exception("Illegal state of the task $name: $state but must be RUNNING")
        }
    }

    // Запушенная задача переходит в состояние suspended
    private fun terminate() {
        if (state == State.RUNNING) {
            state = State.SUSPENDED
        } else {
            throw Exception("Illegal state of the task $name: $state but must be RUNNING")
        }
    }

    override fun run() {
        Thread.sleep(executionTime)
        terminate()
    }

    override fun toString(): String {
        return "BasicTask(name=$name, type=Basic, state=$state, priority=$priority, executionTime=$executionTime, " +
                "suspendingTime=$suspendingTime)"
    }

    fun decreaseSuspendingTime() {
        suspendingTime -= 1
        if (suspendingTime <= 0) {
            activate()
        }
    }
}
