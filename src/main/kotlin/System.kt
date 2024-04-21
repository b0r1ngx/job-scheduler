import task.ExtendedTask
import task.State
import task.Task
import java.util.Stack
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class System {
    var suspendedTasks = listOf<Task>()
        private set

    val terminatedTasks = mutableListOf<Task>()

    val waitingTasks = Stack<Pair<ExtendedTask, Boolean>>()

    private val logService = LogService()
    val queue = Queue(
        logService = logService
    )
    private val processor = Processor(
        onTaskTerminated = terminatedTasks::add,
        onTaskWaiting = waitingTasks::push,
        logService = logService
    )
    private val scheduler = Scheduler(
        queue = queue,
        processor = processor,
        logService = logService,
        waitingTasks = waitingTasks,
        popWaitingTask = waitingTasks::pop,
        pushWaitedTask = waitingTasks::push
    )

    private val thread: ExecutorService = Executors.newSingleThreadExecutor()

    // TODO: We need to understand where to fill this list, that we compare in tests!

    val isTasksEnded = { suspendedTasks.isNotEmpty() || waitingTasks.isNotEmpty() || queue.size != 0 }

    fun run() {
        thread.submit {
            while (isTasksEnded()) {
                Thread.sleep(1)
                decreaseSuspendedTasksTimeAndMoveReadyTasksToQueue()
            }
            logService.systemTermination()
        }

        while (isTasksEnded()) {
            scheduler.run(isTasksEnded)
        }

        terminationDelay()
    }

    // sleep value must be more than execution time of last executed task
    private fun terminationDelay() = Thread.sleep(1000)

    fun decreaseSuspendedTasksTimeAndMoveReadyTasksToQueue() {
        suspendedTasks.forEach { task ->
            task.decreaseSuspendingTime()
            if (task.state == State.READY) {
                queue.add(task).also {
                    suspendedTasks = suspendedTasks.filter { x -> x != task }
                    logService.systemActivatedTask(task, suspendedTasks.size)
                }
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
}
