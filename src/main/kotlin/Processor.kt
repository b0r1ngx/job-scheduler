import task.Task
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Processor(
    private val onTermination: (task: Task) -> Unit,
    private val logService: LogService,
) {
    private var thread: ExecutorService = Executors.newSingleThreadExecutor()
    var isFree = true
        private set

    fun submit(task: Task, additionalInstructionsOnTermination: List<() -> Unit>) {
        logService.processorStartOfTaskExecution(task)
        isFree = false
        task.setPostRunAction(additionalInstructionsOnTermination)
        thread.submit(task)
    }

    fun shutdownNow() {
        logService.processorThreadShutdown()

        thread.shutdownNow()
        thread = Executors.newSingleThreadExecutor()
        isFree = true

        logService.processorThreadInitialization()
    }

    private fun Task.setPostRunAction(additionalInstructionsOnTaskTerminated: List<() -> Unit>) {
        onTermination = {
            additionalInstructionsOnTaskTerminated.forEach {
                it.invoke()
            }
            onTermination(this)
            isFree = true
            logService.processorFinishOfTaskExecution(this)
        }
    }
}