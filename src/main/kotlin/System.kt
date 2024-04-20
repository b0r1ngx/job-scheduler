import task.BasicTask
import task.Priority
import task.State
import task.Task
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class System {
    private var suspendedTasks = listOf<Task>()

    private val queue = Queue()
    private val processor = Processor()
    private val scheduler = Scheduler(queue, processor)

    private val thread: ExecutorService = Executors.newSingleThreadExecutor()

    // TODO: We need to understand where to fill this list, that we compare in tests!
    val terminatedTasks = listOf<Task>()

    fun run() {
        thread.execute {
            while (true) {
                Thread.sleep(10)
                decreaseSuspendedTasksTimeAndMoveReadyTasksToQueue()
            }
        }

        while (suspendedTasks.isNotEmpty() || queue.size != 0) {
            scheduler.run()
        }
    }

    private fun decreaseSuspendedTasksTimeAndMoveReadyTasksToQueue() {
        suspendedTasks.forEach {
            it.decreaseSuspendingTime()
            if (it.state == State.READY) {
                println("Add task: $it to queue")
                suspendedTasks = suspendedTasks.filter { x -> x != it }
                queue.add(it)
            }
        }
    }

    fun addTasks(tasks: List<Task>) {
        // TODO: if we want add some logic to `addTask()`, use: tasks.forEach { addTask(it) }
        suspendedTasks = suspendedTasks + tasks
    }

    fun addTask(task: Task) {
        suspendedTasks = suspendedTasks + listOf(task)
    }
}

fun main() {
    val expectedOrderOfTaskTermination = mutableListOf<Task>()
    var multiplier = 2
    Priority.entries.forEach {
        multiplier *= 2
        expectedOrderOfTaskTermination.add(
            BasicTask(priority = it, suspendingTime = 1L * multiplier)
        )
    }

    val system = System()
    system.addTasks(expectedOrderOfTaskTermination)
    system.run()
}
