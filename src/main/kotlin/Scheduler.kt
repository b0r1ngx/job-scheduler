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

//            println(currentTaskOnExecution)
            if (!isHigherPriorityTaskAppeared) continue
            println(currentTaskOnExecution)

            // TODO: if it may be helpful: here we can collect info of task that was shutdown.
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