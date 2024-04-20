import task.Task
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Processor {
    private var thread: ExecutorService = Executors.newSingleThreadExecutor()
    var isProcessorCaptured = false
        private set

    fun execute(task: Task) {
        isProcessorCaptured = true
        val result = thread.submit(task).get()
        if (result == null) {
            isProcessorCaptured = false
        }
    }

    fun shutdownNow() {
        println("PROCESSOR: shutted down")
        thread.shutdownNow()
        thread = Executors.newSingleThreadExecutor()
        isProcessorCaptured = false
    }
}