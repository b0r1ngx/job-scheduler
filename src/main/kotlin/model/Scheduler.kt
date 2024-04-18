package model

import java.util.PriorityQueue
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.Comparator

class Scheduler {
    private val poolExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val scheduler: ExecutorService = Executors.newSingleThreadExecutor()
    val queue = PriorityBlockingQueue(1, Comparator.comparing(Task::_priority))
//    private val queue = PriorityQueue(Comparator.comparing(Task::_priority))

    fun start(): List<Task> {
        val execution = mutableListOf<Task>()

        scheduler.execute {
            while (queue.isNotEmpty()) {
                try {
                    val task = queue.take()
                    execution.add(task)
                    poolExecutor.execute(task)
                } catch (e: InterruptedException) {
                    println(e.stackTraceToString())
                    break
                }
            }
        }

        return execution
    }

    fun addTask(task: Task) {
        queue.add(task)
    }
}