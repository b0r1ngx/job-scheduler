import task.Task

private const val TAG = "SCHEDULER:"

class Scheduler(
    private val queue: Queue,
    private val processor: Processor
) {
    private var currentExecutingTask: Task? = null

    fun run(isTasksEnded: () -> Boolean) {
        while (isTasksEnded()) {
            if (processor.isFree && queue.size > 0) {
                println("$TAG + Lets occupy processor, because its free & Queue has tasks")
                occupyProcessorBy(task = queue.pop())
            }

            val (isHigherTaskPriorityExists, higherTaskPriority) =
                queue.popHigherTaskPriorityIfExists(currentExecutingTask?.priority)

            if (!isHigherTaskPriorityExists) continue

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