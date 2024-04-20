import task.Priority
import task.Task
import java.util.LinkedList
import java.util.NoSuchElementException

class Queue {
    private val queue = buildMap { Priority.entries.forEach { put(it, LinkedList<Task>()) } }
    private var size: Int = 0

    fun add(task: Task) {
        queue[task.priority]?.add(task)
        size++
    }

    fun pop(): Task {
        if (size > 0) {
            queue.forEach { (_, queue) ->
                try {
                    return queue.pop().also { size-- }
                } catch (e: NoSuchElementException) {
                    return@forEach
                }
            }
        }
        throw NoSuchElementException("Queue is empty")
    }

    fun popHigherTaskIfExists(currentTaskPriority: Priority?): Pair<Boolean, Task?> {
        if (currentTaskPriority != null) {
            queue.forEach { (priority, queue) ->
                if (priority > currentTaskPriority && queue.isNotEmpty())
                    return true to pop()
            }
        }
        return false to null
    }
}
