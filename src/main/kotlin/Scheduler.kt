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
                        logService.schedulerCurrentChoiceFromWaitingStack(chosenTask)
                    }
                    queue.size != 0 -> {
                        chosenTask = queue.pop()
                        if (waitingTasks.isNotEmpty()) {
                            val waitedTask = waitingTasks.pop()
                            waitedTask.release()
                            waitedTasks.push(waitedTask)
                            logService.schedulerMarkTaskWaited(waitedTask)
                        }
                        logService.schedulerCurrentChoiceFromQueue(chosenTask)
                    }
                }

                chosenTask?.let { occupyProcessorBy(it) }
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

        logService.schedulerTerminated()
    }

    private fun occupyProcessorBy(task: Task) {
        currentExecutingTask = task
        processor.submit(task, onWaitEvent)
    }

    private val onWaitEvent = {
        waitingTasks.push(currentExecutingTask as ExtendedTask?)
        currentExecutingTask?.let { logService.processorWaitingAtTask(it) }

        if (queue.size == 0) {
            val chosenTask = waitingTasks.pop()
            chosenTask.release()
            occupyProcessorBy(chosenTask)
            logService.processorContinueExecutionOfWaitedTask(chosenTask)
        }
    }
}