import task.Task
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val TAG = "PROCESSOR:"

class Processor(private val onTaskTerminated: (task: Task) -> Unit) {
    private var thread: ExecutorService = Executors.newSingleThreadExecutor()
    var isFree = true
        private set

    fun submit(task: Task) {
        println("$TAG execute(): $task")
        isFree = false

        task.postRunAction = {
            onTaskTerminated(task)
            isFree = true
        }

        thread.submit(task)
    }

    fun shutdownNow() {
        println("$TAG shutdownNow()")
        thread.shutdownNow()
        thread = Executors.newSingleThreadExecutor()
        isFree = true
    }
}