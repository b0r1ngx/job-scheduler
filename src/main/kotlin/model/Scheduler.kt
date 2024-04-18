package model

import model.task.Task
import java.util.PriorityQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.Comparator

class Scheduler {
    private val poolExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val scheduler: ExecutorService = Executors.newSingleThreadExecutor()
    val queue = PriorityQueue(Comparator.comparing(Task::_priority))

    fun start(): List<Task> {
        val executionOrder = mutableListOf<Task>()

        scheduler.execute {
            while (queue.isNotEmpty()) {
                try {
                    val task = queue.poll()
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