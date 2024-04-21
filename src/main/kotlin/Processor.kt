import task.Task
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Processor(
    val logService: LogService,
    private val onTaskTerminated: (task: Task) -> Unit
) {
    private var thread: ExecutorService = Executors.newSingleThreadExecutor()
    var isFree = true
        private set

    fun submit(task: Task) {
        logService.processorStartOfTaskExecution(task)

        isFree = false
        val terminated = thread.submit(task).get()
        if (terminated == null) {
            onTaskTerminated(task)
            isFree = true
            logService.processorFinishOfTaskExecution(task)
        } else {
            logService.processorErrorWhileTaskExecution(task)
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