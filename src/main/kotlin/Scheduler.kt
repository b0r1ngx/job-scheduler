import task.Task

class Scheduler(
    val queue: Queue,
    private val processor: Processor
) {
    private var currentTaskOnExecution: Task? = null

    fun run() {
        checkIfQueueHasMorePrioritizedTasks()
    }

    private fun checkIfQueueHasMorePrioritizedTasks() {
        while (true) {
            val (isHigherPriorityTaskAppeared, higherPriorityTask) =
                queue.popHigherTaskIfExists(currentTaskPriority = currentTaskOnExecution?.priority)

            if (isHigherPriorityTaskAppeared) continue

            processor.executor.shutdownNow()
            startExecutionOnProcessor(higherPriorityTask!!)
        }
    }

    private fun startExecutionOnProcessor(task: Task) {
        currentTaskOnExecution = task
        processor.executor.execute(task)
    }

    fun addTask(task: Task) {
        queue.add(task)
    }
}