import task.Task
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val TAG = "PROCESSOR:"

class Processor {
    private var thread: ExecutorService = Executors.newSingleThreadExecutor()
    var isFree = true
        private set

    fun execute(task: Task) {
        println("$TAG execute(): $task")
        isFree = false
        thread.execute(task)
        isFree = true
    }

    fun shutdownNow() {
        println("$TAG shutdownNow()")
        thread.shutdownNow()
        thread = Executors.newSingleThreadExecutor()
        isFree = true
    }
}