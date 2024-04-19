import task.Task

class Scheduler(
    val queue: Queue,
    private val processor: Processor
) {
    fun start(): List<Task> {
        val executionOrder = mutableListOf<Task>()

        while (queue.size != 0) {
            try {
                val task = queue.pop()
                executionOrder.add(task)
                processor.executor.execute(task)
            } catch (e: Exception) {
                println(e.stackTraceToString())
                break
            }
        }

        return executionOrder
    }

    fun pick() {
        queue.pop()
    }

    fun addTask(task: Task) {
        queue.add(task)
    }
}