package task

import java.util.UUID

class ExtendedTask(
    priority: Priority = Priority.LOW,
    name: String = UUID.randomUUID().toString(),
    executionTime: Long = 100,
    suspendingTime: Long = 100
) : BasicTask(priority, name, executionTime, suspendingTime) {

    // Выполнение задачи продолжится только после выполнения события
    fun await() {
        if (state == State.RUNNING) {
            state = State.WAITING
        } else {
            throw Exception("Illegal state of the task $name: state was $state but must be RUNNING")
        }
    }

    // Произошло по крайней мере одно событие, которое ожидала задача.
    fun release() {
        if (state == State.WAITING) {
            state = State.READY
        } else {
            throw Exception("Illegal state of the task $name: state was $state but must be WAITING")
        }
    }

    override fun toString() =
        "ExtendedTask(name=$name, state=$state, priority=$priority, executionTime=$executionTime, suspendingTime=$suspendingTime)"
}
