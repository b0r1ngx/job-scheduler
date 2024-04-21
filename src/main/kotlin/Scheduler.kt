import task.Task

private const val TAG = "SCHEDULER:"

class Scheduler(
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
                println("$TAG + start execute on processor, because its free")
                startExecutionOnProcessor(queue.pop())
            }

            val (isHigherPriorityTaskAppeared, higherPriorityTask) =
                queue.popHigherTaskIfExists(currentTaskPriority = currentTaskOnExecution?.priority)

            if (!isHigherPriorityTaskAppeared) continue
            println("$TAG 1s beats 2s - 1s:$higherPriorityTask, 2s:$currentTaskOnExecution")

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