import task.State
import task.Task
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class System {
    val suspendedTasks = mutableListOf<Task>()

    val queue = Queue()
    val processor = Processor()
    val scheduler = Scheduler(queue, processor)

    private val thread: ExecutorService = Executors.newSingleThreadExecutor()

    // TODO: We need to understand where to fill this list, that we compare in tests!
    val terminatedTasks = listOf<Task>()

    init {
        thread.execute {
            while (true) {
                Thread.sleep(1000)
                decreaseSuspendedTasksTimeAndMoveReadyTasksToQueue()
            }
        }

        scheduler.run()
    }

    private fun decreaseSuspendedTasksTimeAndMoveReadyTasksToQueue() {
        suspendedTasks.forEach {
            it.decreaseSuspendingTime()
            if (it.state == State.READY) {
                suspendedTasks.remove(it)
                queue.add(it)
            }
        }
    }
}

fun main() {
    System()
}
