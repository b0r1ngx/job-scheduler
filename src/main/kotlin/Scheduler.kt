import task.Task

class Scheduler(
    val logService: LogService,
    val queue: Queue,
    private val processor: Processor
) {
    private var currentTaskOnExecution: Task? = null

    fun run(isTasksEnded: () -> Boolean) {
        checkIfQueueHasMorePrioritizedTasks(isTasksEnded)
    }

    private fun checkIfQueueHasMorePrioritizedTasks(isTasksEnded: () -> Boolean) {
        while (isTasksEnded()) {
            if (processor.isFree && queue.size > 0) {
                val currentChoice = queue.pop()
                logService.schedulerCurrentChoice(currentChoice)
                startExecutionOnProcessor(currentChoice)
            }

            val (isHigherPriorityTaskAppeared, higherPriorityTask) =
                queue.popHigherTaskIfExists(currentTaskPriority = currentTaskOnExecution?.priority)

            if (!isHigherPriorityTaskAppeared) continue
            currentTaskOnExecution?.let { logService.schedulerStoppingProcessingTask(it) }

            processor.shutdownNow().also {
                currentTaskOnExecution?.preempt().also {
                    queue.add(currentTaskOnExecution!!)
                }
            }
            startExecutionOnProcessor(higherPriorityTask!!)
        }
    }

    private fun startExecutionOnProcessor(task: Task) {
        currentTaskOnExecution = task
        processor.submit(task)
    }
}