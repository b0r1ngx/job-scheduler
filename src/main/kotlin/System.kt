import task.BasicTask
import task.Priority
import task.State
import task.Task
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class System {
    private val queue = Queue()
    private val processor = Processor()
    private val scheduler = Scheduler(queue, processor)

    private val thread: ExecutorService = Executors.newSingleThreadExecutor()

    private var suspendedTasks = listOf<Task>()
    // TODO: We need to understand where to fill this list, that we compare in tests!
    val terminatedTasks = listOf<Task>()

    fun run() {
        thread.submit {
            while (suspendedTasks.isNotEmpty()) {
                decreaseSuspendedTasksTimeAndMoveReadyTasksToQueue()
            }
        }

        while (suspendedTasks.isNotEmpty() || queue.size != 0) {
            scheduler.run()
        }
    }

    private fun decreaseSuspendedTasksTimeAndMoveReadyTasksToQueue() {
        suspendedTasks.forEach {
            Thread.sleep(it.suspendingTime)
            println("SYSTEM: task activated - $it")
            it.activate()
            suspendedTasks = suspendedTasks.filter { x -> x != it }
            queue.add(it)
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
            BasicTask(priority = it, suspendingTime = 10000) // suspendingTime = 1L * multiplier)
        )
    }

    val system = System()
    system.addTasks(expectedOrderOfTaskTermination)
    system.run()
}
