import task.Priority
import task.Task
import java.util.LinkedList
import java.util.NoSuchElementException

private const val TAG = "QUEUE:"

class Queue {
    private val queue: Map<Priority, LinkedList<Task>> = buildMap { Priority.entries.forEach { put(it, LinkedList()) } }

    var size: Int = 0
        private set

    fun add(task: Task) {
        println("$TAG add(): $task")
        queue[task.priority]?.add(task)
        size++
    }

    fun pop(): Task {
        if (size > 0) {
            queue.forEach { (_, queue) ->
                try {
                    return queue.pop().also {
                        println("$TAG pop(): $it")
                        size--
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
                    println("$TAG popHigherPriorityTask")
                    return true to pop()
                }
            }
        }
        return false to null
    }
}
