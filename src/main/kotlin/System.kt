import task.State
import task.Task
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.log

class System {
    val logService = LogService()

    var suspendedTasks = listOf<Task>()
        private set(value) {
            field = value
        }
    val terminatedTasks = mutableListOf<Task>()

    val queue = Queue(logService = logService)
    private val processor = Processor(logService = logService, onTaskTerminated = terminatedTasks::add)
    private val scheduler = Scheduler(logService = logService, queue = queue, processor = processor)

    private val thread: ExecutorService = Executors.newSingleThreadExecutor()

    // TODO: We need to understand where to fill this list, that we compare in tests!

    val isTasksEnded = { suspendedTasks.isNotEmpty() || queue.size != 0 }

    fun run() {
        thread.submit {
            while (isTasksEnded()) {
                Thread.sleep(1)
                decreaseSuspendedTasksTimeAndMoveReadyTasksToQueue()
            }
            logService.systemTermination()
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
                    logService.systemActivatedTask(task, suspendedTasks.size)
                }
        }
    }

    fun addTasks(tasks: List<Task>) {
        // TODO: if we want add some logic to `addTask()`, use: tasks.forEach { addTask(it) }
        suspendedTasks = suspendedTasks + tasks
        logService.systemInit(suspendedTasks.size)
    }

    fun addTask(task: Task) {
        suspendedTasks = suspendedTasks + listOf(task)
    }
}
