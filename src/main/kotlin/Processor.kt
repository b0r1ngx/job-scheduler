import task.ExtendedTask
import task.Task
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Processor(
    private val onTaskTermination: (task: Task) -> Unit,
    private val logService: LogService,
) {
    private var thread: ExecutorService = Executors.newSingleThreadExecutor()
    var isFree = true
        private set

    fun submit(task: Task, onWaitEvent: () -> Unit, additionalInstructionsOnTermination: List<() -> Unit>) {
        logService.processorStartOfTaskExecution(task)
        isFree = false

        task.setOnTerminationActions(additionalInstructionsOnTermination)
        (task as? ExtendedTask)?.setOnWaitAction(onWaitEvent)

        thread.submit(task)
    }

    fun shutdownNow() {
        logService.processorThreadShutdown()

        thread.shutdownNow()
        thread = Executors.newSingleThreadExecutor()
        isFree = true

        logService.processorThreadInitialization()
    }

    private fun Task.setOnTerminationActions(additionalInstructionsOnTaskTerminated: List<() -> Unit>) {
        onTermination = {
            additionalInstructionsOnTaskTerminated.forEach {
                it.invoke()
            }
            onTaskTermination(this)
            isFree = true
            logService.processorFinishOfTaskExecution(this)
        }
    }

    private fun ExtendedTask.setOnWaitAction(onWaitEvent: () -> Unit) {
        waitAction = {
            onWaitEvent()
            isFree = true
            // TODO: add log for waiting action
        }
    }
}