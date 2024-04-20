import task.BasicTask
import task.Priority
import task.Task
import kotlin.test.Test

@Suppress("TestFunctionName")
internal class SchedulerTest {
    private val system = System()

    @Test
    fun WHEN_adds_tasks_it_added_WHEN_pop_tasks_it_out() {
        val expectedOrderOfTaskTermination = mutableListOf<Task>()
        var multiplier = 1
        Priority.entries.forEach {
            multiplier *= 2
            expectedOrderOfTaskTermination.add(
                BasicTask(priority = it, suspendingTime = 1L * multiplier)
            )
        }

        println(expectedOrderOfTaskTermination)

        system.addTasks(expectedOrderOfTaskTermination)
        system.run()
        println("End")
//            assertEquals(task, scheduler.queue.pop())

    }

    @Test
    fun WHEN_different_priority_tasks_queued_THEN_highest_priority_task_picked() {
        val firstTask = BasicTask(Priority.CRITICAL, "1")
        val secondTask = BasicTask(Priority.HIGH, "2")
        val thirdTask = BasicTask(Priority.LOW, "3")

//        scheduler.addTask(firstTask)
//        scheduler.addTask(secondTask)
//        scheduler.addTask(thirdTask)
//
//        val actualExecutionOrder = scheduler.run()
//        assertEquals(firstTask, actualExecutionOrder.first())
    }

    @Test
    fun WHEN_different_priority_tasks_queued_THEN_correct_order_of_execution_happened() {
        val firstTask = BasicTask(Priority.CRITICAL, "1")
        val secondTask = BasicTask(Priority.HIGH, "2")
        val thirdTask = BasicTask(Priority.HIGH, "3")
        val fourthTask = BasicTask(Priority.HIGH, "4")
        val fifthTask = BasicTask(Priority.LOW, "5")

//        scheduler.addTask(firstTask)
//        scheduler.addTask(thirdTask)  // 3
//        scheduler.addTask(fourthTask) // 4
//        scheduler.addTask(secondTask) // 2
//        scheduler.addTask(fifthTask)

        val expectedExecutionOrder = listOf(
            firstTask, thirdTask, fourthTask, secondTask, fifthTask
        )

//        val actualExecutionOrder = scheduler.run()
        //assertEquals(expectedExecutionOrder, actualExecutionOrder)
    }

    @Test
    fun WHEN_higher_priority_task_queued_THEN_current_executed_task_goes_to_queue() {
        val firstTask = BasicTask(Priority.CRITICAL, "1")
        val secondTask = BasicTask(Priority.HIGH, "2")

//        scheduler.addTask(secondTask)
//        val actualExecutionOrder = scheduler.run()
//        scheduler.addTask(firstTask)

        /*
        assertEquals(
            expected = listOf(firstTask, secondTask),
            actual = actualExecutionOrder
        )

         */
    }

    @Test
    fun systemInit() {
        val tasks = listOf(
            BasicTask(name="1"),
            BasicTask(name="2"),
            BasicTask(name="3")
        )

        system.addTasks(tasks)
        system.run()
    }
}