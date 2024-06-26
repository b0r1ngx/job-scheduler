import task.State
import task.Task
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class System {
    val terminatedTasks = mutableListOf<Task>()
    var suspendedTasks = listOf<Task>()
        private set

    private val logService = LogService()

    val queue = Queue(logService = logService)
    private val processor = Processor(onTaskTermination = terminatedTasks::add, logService = logService)
    private val scheduler = Scheduler(queue = queue, processor = processor, logService = logService)

    private val thread: ExecutorService = Executors.newSingleThreadExecutor()

    val isTasksEnded = { suspendedTasks.isNotEmpty() || queue.size != 0 || scheduler.isThereWaitingTasks() }

    fun run() {
        thread.submit {
            while (isTasksEnded()) {
                Thread.sleep(1)
                decreaseSuspendedTasksTimeAndMoveReadyTasksToQueue()
            }
            logService.systemTermination()
        }

        scheduler.run(isTasksEnded)

        terminationDelay()
    }

    fun decreaseSuspendedTasksTimeAndMoveReadyTasksToQueue() {
        suspendedTasks.forEach { task ->
            task.decreaseSuspendingTime()
            if (task.state == State.READY) {
                logService.systemActivatedTask(task, suspendedTasks.size - 1)
                queue.add(task)
                suspendedTasks = suspendedTasks.filter { x -> x != task }
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
        logService.systemInit(suspendedTasks.size)
    }

    // sleep value must be more than execution time of last executed task
    private fun terminationDelay() {
        logService.systemTerminationDelay()
        Thread.sleep(1000)
    }
}
