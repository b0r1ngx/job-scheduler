import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Processor {
    val executor: ExecutorService = Executors.newSingleThreadExecutor()
    var isFree = true

    /*
    fun acquireTask(task: Task) {
        isFree = false
        executor.execute(task)
        isFree = true
    }

     */
}