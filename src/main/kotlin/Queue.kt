import task.Priority
import task.Task
import java.util.LinkedList
import java.util.NoSuchElementException

class Queue(private val logService: LogService) {
    private val queue: Map<Priority, LinkedList<Task>> = buildMap { Priority.entries.forEach { put(it, LinkedList()) } }

    var size: Int = 0
        private set

    fun add(task: Task) {
        queue[task.priority]?.add(task)
        size++
        logService.systemTaskPushedToQueue(task)
    }

    fun pop(): Task {
        if (size > 0) {
            queue.forEach { (_, queue) ->
                try {
                    return queue.pop().also {
                        size--
                        logService.schedulerTaskPoppedFromQueue(it)
                    }
                } catch (e: NoSuchElementException) {
                    return@forEach
                }
            }
        }
        throw NoSuchElementException("Queue is empty")
    }

    fun popHigherTaskPriorityIfExists(currentTaskPriority: Priority?): Pair<Boolean, Task?> {
        if (currentTaskPriority != null) {
            queue.forEach { (priority, queue) ->
                if (priority > currentTaskPriority && queue.isNotEmpty()) {
                    val task = pop()
                    logService.schedulerPoppedTaskWithHigherPriority(task)
                    return true to task
                }
            }
        }
        return false to null
    }
}
