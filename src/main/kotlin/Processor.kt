import task.Task
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Processor {
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    var isFree = true
        private set

    fun execute(task: Task) {
        isFree = false
        executor.execute(task)
        isFree = true
    }

    fun shutdownNow() {
        executor.shutdownNow()
    }
}