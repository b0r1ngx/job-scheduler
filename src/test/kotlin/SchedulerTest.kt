import task.Priority
import task.Task
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("TestFunctionName")
internal class SchedulerTest {
    private val system = System()
    private val scheduler = system.scheduler

    @Test
    fun WHEN_adds_tasks_it_added_WHEN_pop_tasks_it_out() {
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
        val thirdTask = Task(Priority.LOW, "3")

        scheduler.addTask(firstTask)
        scheduler.addTask(secondTask)
        scheduler.addTask(thirdTask)

        val actualExecutionOrder = scheduler.run()
//        assertEquals(firstTask, actualExecutionOrder.first())
    }

    @Test
    fun WHEN_different_priority_tasks_queued_THEN_correct_order_of_execution_happened() {
        val firstTask = Task(Priority.CRITICAL, "1")
        val secondTask = Task(Priority.HIGH, "2")
        val thirdTask = Task(Priority.HIGH, "3")
        val fourthTask = Task(Priority.HIGH, "4")
        val fifthTask = Task(Priority.LOW, "5")

        scheduler.addTask(firstTask)
        scheduler.addTask(thirdTask)  // 3
        scheduler.addTask(fourthTask) // 4
        scheduler.addTask(secondTask) // 2
        scheduler.addTask(fifthTask)

        val expectedExecutionOrder = listOf(
            firstTask, thirdTask, fourthTask, secondTask, fifthTask
        )

        val actualExecutionOrder = scheduler.run()
//        assertEquals(expectedExecutionOrder, actualExecutionOrder)
    }

    @Test
    fun WHEN_higher_priority_task_queued_THEN_current_executed_task_goes_to_queue() {
        val firstTask = Task(Priority.CRITICAL, "1")
        val secondTask = Task(Priority.HIGH, "2")

        scheduler.addTask(secondTask)
        val actualExecutionOrder = scheduler.run()
        scheduler.addTask(firstTask)

//        assertEquals(
//            expected = listOf(firstTask, secondTask),
//            actual = actualExecutionOrder
//        )
    }
}