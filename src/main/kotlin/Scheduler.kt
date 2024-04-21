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
            if (processor.isFree && queue.size > 0)
                startExecutionOnProcessor(queue.pop())

            val (isHigherPriorityTaskAppeared, higherPriorityTask) =
                queue.popHigherTaskIfExists(currentTaskPriority = currentTaskOnExecution?.priority)

//            println(currentTaskOnExecution)
            if (!isHigherPriorityTaskAppeared) continue
            println("$higherPriorityTask ////_WANT TO BEAT_//// $currentTaskOnExecution")

            // TODO: if it may be helpful: here we can collect info of task that was shutdown.

            processor.shutdownNow().also {
                currentTaskOnExecution?.terminate().also {
                    currentTaskOnExecution?.activate().also {
                        queue.add(currentTaskOnExecution!!)
                    }
                }
            }
            startExecutionOnProcessor(higherPriorityTask!!)
        }
    }

    private fun startExecutionOnProcessor(task: Task) {
        println("startExecutionOnProcessor() with Task: $task")
        currentTaskOnExecution = task
        processor.execute(task)
    }
}