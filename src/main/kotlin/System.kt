import task.State
import task.Task
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val TAG = "SYSTEM:"

class System {
    var suspendedTasks = listOf<Task>()
        private set(value) {
            println("$TAG suspendedTasks.size: ${value.size}")
            field = value
        }

    val terminatedTasks = mutableListOf<Task>()

    val queue = Queue()
    private val processor = Processor(onTaskTerminated = terminatedTasks::add)
    private val scheduler = Scheduler(queue, processor)

    private val thread: ExecutorService = Executors.newSingleThreadExecutor()

    // TODO: We need to understand where to fill this list, that we compare in tests!

    val isTasksEnded = { suspendedTasks.isNotEmpty() || queue.size != 0 }

    fun run() {
        thread.submit {
            while (isTasksEnded()) {
                Thread.sleep(1)
                decreaseSuspendedTasksTimeAndMoveReadyTasksToQueue()
            }
            println("$TAG don't have tasks in suspend list or queue - terminate system.")
        }

        while (isTasksEnded()) {
            scheduler.run(isTasksEnded = isTasksEnded)
        }
    }

    fun decreaseSuspendedTasksTimeAndMoveReadyTasksToQueue() {
        suspendedTasks.forEach { task ->
            task.decreaseSuspendingTime()
            if (task.state == State.READY)
                queue.add(task).also {
                    suspendedTasks = suspendedTasks.filter { x -> x != task }
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
