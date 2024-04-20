import task.Task

class Scheduler(
    val queue: Queue,
    private val processor: Processor
) {
    fun run(): List<Task> {
        val executionOrder = mutableListOf<Task>()

        while (true) {
            val isQueueEmpty = queue.size != 0

            if (isQueueEmpty) {
                continue
            }

            // TODO: For Kirill, If current executed task.priority on processor is lower, than appeared task in Queue,
            //  processor must pause, task must goes to State.Suspended, and processor must execute new task

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