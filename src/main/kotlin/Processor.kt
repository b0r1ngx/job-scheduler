import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Processor {
    val executor: ExecutorService = Executors.newSingleThreadExecutor()
}