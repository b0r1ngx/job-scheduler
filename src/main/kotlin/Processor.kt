import task.ExtendedTask
import task.Task
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Processor(
    private val onTaskTerminated: (task: Task) -> Unit,
    val logService: LogService,
) {
    private var thread: ExecutorService = Executors.newSingleThreadExecutor()
    var isFree = true
        private set

    fun submit(task: Task, onWaitEvent: () -> Unit) {
        logService.processorStartOfTaskExecution(task)

        isFree = false

        task.postRunAction = {
            onTaskTerminated(task)
            isFree = true
            logService.processorFinishOfTaskExecution(task)
        }
        if (task is ExtendedTask) {
            task.waitAction = {
                onWaitEvent()
                isFree = true
                // TODO: add log for waiting action
            }
        }

        thread.submit(task)
    }

    fun shutdownNow() {
        logService.processorThreadShutdown()

        thread.shutdownNow()
        thread = Executors.newSingleThreadExecutor()
        isFree = true

        logService.processorThreadInitialization()
    }
}