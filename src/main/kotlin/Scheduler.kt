import task.ExtendedTask
import task.Task
import java.util.*

class Scheduler(
    private val queue: Queue,
    private val processor: Processor,
    private val logService: LogService,
    private val waitingTasks: Stack<Pair<ExtendedTask, Boolean>>,
    private val popWaitingTask: () -> Pair<ExtendedTask, Boolean>,
    private val pushWaitedTask: (Pair<ExtendedTask, Boolean>) -> Unit
) {
    private var currentExecutingTask: Task? = null

    fun run(isTasksEnded: () -> Boolean) {
        while (isTasksEnded()) {
            if (processor.isFree) {
                var chosenTask: Task? = null

                for (entry in waitingTasks) {
                    if (entry.second) {
                        chosenTask = popWaitingTask().first
                        // TODO: log getting of waited task
                        break
                    }
                }
                if (chosenTask == null && queue.size != 0) {
                    chosenTask = queue.pop()
                    if (waitingTasks.isNotEmpty()) {
                        val waitedTask = popWaitingTask().first
                        waitedTask.release()
                        pushWaitedTask(waitedTask to true)
                        // TODO: log setting of wait
                    }
                }

                chosenTask?.let {
                    logService.schedulerCurrentChoice(it)
                    occupyProcessorBy(task = it)
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
    }

    private fun occupyProcessorBy(task: Task) {
        currentExecutingTask = task
        processor.submit(task)
    }
}