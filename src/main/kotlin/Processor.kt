import task.Task
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Processor {
    private var thread: ExecutorService = Executors.newSingleThreadExecutor()
    var isFree = true
        private set

    fun execute(task: Task) {
        isFree = false
        thread.execute(task)
        isFree = true
    }

    fun shutdownNow() {
        thread.shutdownNow()
        thread = Executors.newSingleThreadExecutor()
        isFree = true
    }
}