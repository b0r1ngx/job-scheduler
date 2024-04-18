package model

import model.task.Task
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Scheduler {
    private val scheduler: ExecutorService = Executors.newSingleThreadExecutor()
    private val poolExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    val queue = Queue()

    fun start(): List<Task> {
        val executionOrder = mutableListOf<Task>()

        scheduler.execute {
            while (queue.size != 0) {
                try {
                    val task = queue.pop()
                    executionOrder.add(task)
                    poolExecutor.execute(task)
                } catch (e: InterruptedException) {
                    println(e.stackTraceToString())
                    break
                }
            }
        }

        return executionOrder
    }

    fun addTask(task: Task) {
        queue.add(task)
    }
}