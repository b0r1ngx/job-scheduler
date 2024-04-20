import task.Task
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Scheduler(
    private val queue: Queue,
    private val processor: Processor
) {
    private var currentTaskOnExecution: Task? = null

    fun run() {
        if (!processor.isProcessorCaptured && queue.size != 0) {
            val task = queue.pop()
            println("SCHEDULER: current choice - $task")
            executeTaskOnProcessor(task)
        }

        //checkIfQueueHasMorePrioritizedTasks()
    }

    private fun executeTaskOnProcessor(task: Task) {
        currentTaskOnExecution = task
        processor.execute(task)
    }

    private fun checkIfQueueHasMorePrioritizedTasks() {
        while (true) {
            val (isHigherPriorityTaskAppeared, higherPriorityTask) =
                queue.popHigherTaskIfExists(currentTaskPriority = currentTaskOnExecution?.priority)

//            println(currentTaskOnExecution)
            if (!isHigherPriorityTaskAppeared) continue
            println(currentTaskOnExecution)

            // TODO: if it may be helpful: here we can collect info of task that was shutdown.
            //processor.executor.shutdownNow()
            executeTaskOnProcessor(higherPriorityTask!!)
        }
    }

    fun addTask(task: Task) {
        queue.add(task)
    }
}