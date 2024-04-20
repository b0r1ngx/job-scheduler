import task.Task

class Scheduler(
    val queue: Queue,
    private val processor: Processor
) {
    private var currentTaskOnExecution: Task? = null

    fun run() {
        while (true) {
            checkIfQueueHasMorePrioritizedTasks()
        }
    }

    private fun checkIfQueueHasMorePrioritizedTasks() {
        while (true) {
            val (isHigherPriorityTaskAppeared, higherPriorityTask) =
                queue.hasTasksWithPriorityHigherThanCurrentTaskThenPopHigherTask(
                    currentTask = currentTaskOnExecution?.priority
                )

            if (isHigherPriorityTaskAppeared) continue

            processor.executor.shutdownNow()
            startExecutionOnProcessor(higherPriorityTask!!)
            // check if queue.hasPriorityWithHigherTask, that current task
            // drop current task from processor
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