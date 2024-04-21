import task.Task

class Scheduler(
    val logService: LogService,
    val queue: Queue,
    private val processor: Processor
) {
    private var currentExecutingTask: Task? = null

    fun run(isTasksEnded: () -> Boolean) {
        while (isTasksEnded()) {
            if (processor.isFree && queue.size > 0) {
                val currentChoice = queue.pop()
                logService.schedulerCurrentChoice(currentChoice)
                startExecutionOnProcessor(currentChoice)
            }

            val (isHigherTaskPriorityExists, higherTaskPriority) =
                queue.popHigherTaskPriorityIfExists(currentExecutingTask?.priority)

            if (!isHigherPriorityTaskAppeared) continue
            currentTaskOnExecution?.let { logService.schedulerStoppingProcessingTask(it) }

            println("$TAG $higherTaskPriority \$-_BEATS_-\$ $currentExecutingTask")
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
        processor.submit(task)
    }
}