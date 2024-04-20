package task

import statemachine.State
import java.util.*

class ExtendedTask(
    priority: Priority = Priority.LOW,
    name: String = UUID.randomUUID().toString(),
    executionTime: Long = 1000,
    suspendingTime: Long = 1000
) : BasicTask(priority, name, executionTime, suspendingTime) {

    // Переход в состояние ожидания. Выполнение задачи продолжится только после выполнения события
    fun waitSM() {
        if (state == State.RUNNING) {
            state = State.WAITING
        } else {
            throw Exception("Illegal state of the task $name: $state but must be RUNNING")
        }
    }

    fun releaseSM() {
        if (state == State.WAITING) {
            state = State.READY
        } else {
            throw Exception("Illegal state of the task $name: $state but must be WAITING")
        }
    }

}