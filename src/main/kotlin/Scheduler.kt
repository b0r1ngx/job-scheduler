import task.Task
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Scheduler {
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    val queue = Queue()

    fun start(): List<Task> {
        val executionOrder = mutableListOf<Task>()

        while (queue.size != 0) {
            try {
                val task = queue.pop()
                executionOrder.add(task)
                executor.execute(task)
            } catch (e: Exception) {
                println(e.stackTraceToString())
                break
            }
        }

        return executionOrder
    }

    fun addTask(task: Task) {
        queue.add(task)
    }
}