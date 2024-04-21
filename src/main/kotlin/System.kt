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

    val isTasksEnded = { suspendedTasks.isNotEmpty() || queue.size != 0 }

    fun run() {
        thread.execute {
            while (isTasksEnded()) {
                Thread.sleep(10)
                decreaseSuspendedTasksTimeAndMoveReadyTasksToQueue()
            }
            println("System don't have tasks in suspend list or queue - terminate system.")
        }

        while (isTasksEnded()) {
            scheduler.run()
        }
    }

    private fun decreaseSuspendedTasksTimeAndMoveReadyTasksToQueue() {
        suspendedTasks.forEach { task ->
            task.decreaseSuspendingTime()
            if (task.state == State.READY) {
                queue.add(task).also {
                    suspendedTasks = suspendedTasks.filter { x -> x != task }
                    println("Added task to queue, delete it from suspendedTasks: $task")
                }
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
    val expectedOrderOfTaskTermination = listOf<Task>(
        BasicTask(priority = Priority.LOW, suspendingTime = 200),
        BasicTask(priority = Priority.MEDIUM, suspendingTime = 400),
        BasicTask(priority = Priority.HIGH, suspendingTime = 800),
        BasicTask(priority = Priority.CRITICAL, suspendingTime = 1600)
    )

    val system = System()
    system.addTasks(expectedOrderOfTaskTermination)
    system.run()
}
