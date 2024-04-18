import model.Scheduler
import model.task.Priority
import model.task.Task
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("TestFunctionName")
internal class SchedulerTest {

    private val scheduler = Scheduler()

    @Test
    fun addTasks() {
        Priority.entries.forEach {
            val task = Task(priority = it)
            scheduler.addTask(task = task)
            assertEquals(task, scheduler.queue.pop())
        }
    }

    @Test
    fun WHEN_different_priority_tasks_queued_THEN_highest_priority_task_picked() {
        val firstTask = Task(Priority.CRITICAL, "1")
        val secondTask = Task(Priority.HIGH, "2")
        val thirdTask = Task(Priority.HIGH, "3")
        val fourthTask = Task(Priority.HIGH, "4")
        val fifthTask = Task(Priority.LOW, "5")

        scheduler.addTask(firstTask)
        scheduler.addTask(thirdTask) // 3
        scheduler.addTask(fourthTask) // 4
        scheduler.addTask(secondTask) // 2
        scheduler.addTask(fifthTask)

        val expectedExecutionOrder = listOf(
            firstTask, thirdTask, fourthTask, secondTask, fifthTask
        )

        val actualExecutionOrder = scheduler.start()
        assertEquals(expectedExecutionOrder, actualExecutionOrder)
    }

    @Test
    fun WHEN_tasks_more_than_capacity_added_THEN_scheduler_works_correct() {
        repeat(20) {
            scheduler.addTask(Task(Priority.entries.random()))
        }
        scheduler.start()
    }
}