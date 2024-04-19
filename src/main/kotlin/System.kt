import task.Task

class System {
    val queue = Queue()
    val processor = Processor()
    val scheduler = Scheduler(queue, processor)

    val terminatedTasks = listOf<Task>()

    init {
        scheduler.start()
    }
}
