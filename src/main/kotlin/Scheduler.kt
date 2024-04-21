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
                println("$TAG + start execute on processor, because its free")
                occupyProcessorBy(task = queue.pop())
            }

            val (isHigherPriorityTaskAppeared, higherPriorityTask) =
                queue.popHigherTaskPriorityIfExists(currentExecutingTask?.priority)

            if (!isHigherPriorityTaskAppeared) continue

            println("$TAG $higherPriorityTask \$-_BEATS_-\$ $currentExecutingTask")
            processor.shutdownNow().also {
                currentExecutingTask?.preempt().also {
                    queue.add(currentExecutingTask!!)
                }
            }
            occupyProcessorBy(task = higherPriorityTask!!)
        }
    }

    private fun occupyProcessorBy(task: Task) {
        currentExecutingTask = task
        processor.submit(task)
    }
}