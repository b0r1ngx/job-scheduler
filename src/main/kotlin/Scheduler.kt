import task.ExtendedTask
import task.Task
import java.util.*

class Scheduler(
    private val queue: Queue,
    private val processor: Processor,
    private val logService: LogService,
) {

    private var currentExecutingTask: Task? = null

    private val waitingTasks = Stack<ExtendedTask>()
    private val waitedTasks = Stack<ExtendedTask>()

    val isThereWaitingTasks = { waitingTasks.isNotEmpty() || waitedTasks.isNotEmpty() }

    fun run(isTasksEnded: () -> Boolean) {
        while (isTasksEnded()) {
            if (processor.isFree) {
                var chosenTask: Task? = null

                when {
                    waitedTasks.isNotEmpty() -> {
                        chosenTask = waitedTasks.pop()
                        println("lalala")
                        // TODO
                    }
                    queue.size != 0 -> {
                        chosenTask = queue.pop()
                        if (waitingTasks.isNotEmpty()) {
                            val waitedTask = waitingTasks.pop()
                            waitedTask.release()
                            waitedTasks.push(waitedTask)
                            // TODO
                        }
                    }
                    /*
                    queue.size == 0 && waitingTasks.isNotEmpty() -> {
                        chosenTask = waitingTasks.pop()
                        chosenTask.release()
                    }

                     */
                    /*
                    waitedTasks.isEmpty() && queue.size == 0 && waitingTasks.isNotEmpty() -> {
                        chosenTask = waitingTasks.pop()
                        chosenTask.release()
                        // TODO
                    }

                     */
                }

                if (chosenTask != null) {
                    occupyProcessorBy(chosenTask)
                }
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
        // TODO
    }

    private fun occupyProcessorBy(task: Task) {
        logService.schedulerCurrentChoice(task)

        currentExecutingTask = task
        processor.submit(task, onWaitEvent)
    }

    private val onWaitEvent = {
        waitingTasks.push(currentExecutingTask as ExtendedTask?)
        /*
        if (queue.size == 0) {
            (currentExecutingTask as ExtendedTask?)?.release()
            waitedTasks.push(currentExecutingTask as ExtendedTask?)
        } else {
            waitingTasks.push(currentExecutingTask as ExtendedTask?)
        }

         */

        println(waitedTasks.joinToString())                          // TODO
    }
}