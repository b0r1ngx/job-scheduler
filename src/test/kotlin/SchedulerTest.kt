import task.BasicTask
import task.ExtendedTask
import task.Priority
import task.Task
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// TODO: Rename it to SystemTest.kt
@Suppress("TestFunctionName")
internal class SchedulerTest {
    private val system = System()

    private fun run(initialTasks: List<Task>) =
        with(system) {
            terminatedTasks.clear()
            addTasks(initialTasks)
            run()
        }

    @Test
    fun WHEN_add_tasks_they_are_added() {
        assertTrue(system.suspendedTasks.isEmpty())
        system.addTask(BasicTask())
        assertTrue(system.suspendedTasks.isNotEmpty())
    }

    @Test
    fun WHEN_system_thread_logic_executes_THEN_task_goes_from_suspendedTasks_to_queue() {
        assertTrue(system.suspendedTasks.isEmpty())
        system.addTask(BasicTask(suspendingTime = 1))
        assertTrue(system.queue.size == 0)
        assertTrue(system.suspendedTasks.isNotEmpty())
        system.decreaseSuspendedTasksTimeAndMoveReadyTasksToQueue()
        assertTrue(system.queue.size != 0)
        assertTrue(system.suspendedTasks.isEmpty())
    }

    @Test
    fun WHEN_pop_tasks_it_out() {
        assertTrue(system.queue.size == 0)

        system.queue.add(BasicTask())

        assertTrue(system.queue.size != 0)

        system.queue.pop()
        assertTrue(system.queue.size == 0)
    }

    @Test
    fun START_two_same_priority_tasks_THEN_system_execute_them_in_correct_order() {
        val input = listOf<Task>(BasicTask(), BasicTask())
        run(initialTasks = input)
        assertEquals(expected = input, actual = system.terminatedTasks)
    }

    @Test
    fun WHEN_different_priority_tasks_queued_THEN_system_execute_them_in_correct_order() {
        val input = listOf<Task>(
            BasicTask(Priority.CRITICAL, "1"),
            BasicTask(Priority.HIGH, "2"),
            BasicTask(Priority.HIGH, "3"),
            BasicTask(Priority.HIGH, "4"),
            BasicTask(Priority.LOW, "5"),
        )
        run(initialTasks = input)
        assertEquals(expected = input, actual = system.terminatedTasks)
    }

    @Test
    fun START_various_tasks_THEN_system_executes_them_in_correct_order() {
        val input = listOf<Task>(
            BasicTask(name = "1", priority = Priority.LOW, suspendingTime = 200),
            BasicTask(name = "2", priority = Priority.MEDIUM, suspendingTime = 400),
            BasicTask(name = "3", priority = Priority.HIGH, suspendingTime = 600),
            BasicTask(name = "4", priority = Priority.CRITICAL, suspendingTime = 800),
        )
        run(initialTasks = input)
        assertEquals(expected = input, actual = system.terminatedTasks)
    }

    @Test
    fun WHEN_higher_priority_task_queued_THEN_current_executed_task_goes_to_queue() {
        val input = listOf<Task>(
            BasicTask(Priority.HIGH, "1", suspendingTime = 10),
            BasicTask(Priority.CRITICAL, "2", suspendingTime = 50),
        )
        run(initialTasks = input)
        assertEquals(expected = input.reversed(), actual = system.terminatedTasks)
    }

    @Test
    fun START_various_tasks_THEN_system_executes_them_in_correct_order_medium_case() {
        val input = listOf<Task>(
            BasicTask(name = "1", priority = Priority.LOW, suspendingTime = 20),
            BasicTask(name = "2", priority = Priority.MEDIUM, suspendingTime = 40),
            BasicTask(name = "3", priority = Priority.HIGH, suspendingTime = 60),
            BasicTask(name = "4", priority = Priority.CRITICAL, suspendingTime = 80),
        )
        run(initialTasks = input)
        assertEquals(expected = input.reversed(), actual = system.terminatedTasks)
    }

    @Test
    fun START_various_tasks_THEN_system_executes_them_in_correct_order_medium_case_longer_sus_time() {
        val expectedTerminationOrder = listOf<Task>(
            BasicTask(name = "4", priority = Priority.CRITICAL, suspendingTime = 800), // 80
            BasicTask(name = "3", priority = Priority.HIGH, suspendingTime = 600), // 60
            BasicTask(name = "2", priority = Priority.MEDIUM, suspendingTime = 400),
            BasicTask(name = "1", priority = Priority.LOW, suspendingTime = 200),
        )
        run(initialTasks = expectedTerminationOrder)
        assertEquals(expected = expectedTerminationOrder, actual = system.terminatedTasks)
    }

    @Test
    fun START_extended_task_dont_change_the_order() {
        val task1 = BasicTask(name = "1", priority = Priority.LOW, suspendingTime = 100)
        val task2 = ExtendedTask(name = "2", priority = Priority.LOW, suspendingTime = 200, untilWaitTime = 50)
        val task3 = BasicTask(name = "3", priority = Priority.LOW, suspendingTime = 400)

        val input = listOf<Task>(task1, task2, task3)
        val expectedTerminationOrder = listOf<Task>(task1, task2, task3)

        run(initialTasks = input)
        assertEquals(expected = expectedTerminationOrder, actual = system.terminatedTasks)
    }

    @Test
    fun START_extended_task_change_the_order() {
        val task1 = BasicTask(name = "1", priority = Priority.LOW, suspendingTime = 100)
        val task2 = ExtendedTask(name = "2", priority = Priority.LOW, suspendingTime = 200, untilWaitTime = 50)
        val task3 = BasicTask(name = "3", priority = Priority.LOW, suspendingTime = 210)

        val input = listOf<Task>(task1, task2, task3)
        val expectedTerminationOrder = listOf<Task>(task1, task3, task2)

        run(initialTasks = input)
        assertEquals(expected = expectedTerminationOrder, actual = system.terminatedTasks)
    }

    @Test
    fun START_extended_task_in_the_end_of_the_order() {
        val task1 = BasicTask(name = "1", priority = Priority.LOW, suspendingTime = 100)
        val task2 = ExtendedTask(name = "2", priority = Priority.LOW, suspendingTime = 200, untilWaitTime = 50)

        val input = listOf<Task>(task1, task2)
        val expectedTerminationOrder = listOf<Task>(task1, task2)

        run(initialTasks = input)
        assertEquals(expected = expectedTerminationOrder, actual = system.terminatedTasks)
    }
}