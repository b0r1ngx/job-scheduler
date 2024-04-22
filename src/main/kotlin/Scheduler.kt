import task.Task

class Scheduler(
    private val queue: Queue,
    private val processor: Processor,
    private val logService: LogService,
) {
    private var currentExecutingTask: Task? = null

    fun run(isTasksEnded: () -> Boolean) {
        while (isTasksEnded()) {
            if (processor.isFree && queue.size > 0) {
                val chosenTask = queue.pop()
                logService.schedulerCurrentChoice(chosenTask)
                occupyProcessorBy(task = chosenTask)
            }

            val (isHigherTaskPriorityExists, higherTaskPriority) =
                queue.popHigherTaskPriorityIfExists(currentExecutingTask?.priority)

            if (!isHigherTaskPriorityExists) continue

            currentExecutingTask?.let { logService.schedulerStoppingProcessingTask(it) }
            processor.shutdownNow().also {
                currentExecutingTask?.preempt().also {
                    queue.add(currentExecutingTask!!)
                }
            }
            occupyProcessorBy(task = higherTaskPriority!!)
        }
    }

    private fun occupyProcessorBy(task: Task) {
        currentExecutingTask = task
        processor.submit(
            task = task,
            additionalInstructionsOnTermination = listOf { currentExecutingTask = null }
        )
    }
}