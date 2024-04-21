import task.Task
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val TAG = "PROCESSOR:"

class Processor(private val onTaskTerminated: (task: Task) -> Unit) {
    private var thread: ExecutorService = Executors.newSingleThreadExecutor()
    var isFree = true
        private set

    fun execute(task: Task) {
        println("$TAG execute(): $task")
        isFree = false
//        thread.execute(task)
//        isFree = true

        val terminated = thread.submit(task).get()
        if (terminated == null) {
            onTaskTerminated(task)
            isFree = true
            println("$TAG end execute(): $task")
        } else {
            println("$TAG is it happens when we shutdown, or before task is executed on thread")
        }
    }

    fun shutdownNow() {
        println("$TAG shutdownNow()")
        thread.shutdownNow()
        thread = Executors.newSingleThreadExecutor()
        isFree = true
    }
}