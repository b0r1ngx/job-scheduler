import task.BasicTask
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
        val expectedTerminationOrder = listOf<Task>(BasicTask(), BasicTask())
        run(initialTasks = expectedTerminationOrder)
        assertEquals(expected = expectedTerminationOrder, actual = system.terminatedTasks)
    }

    @Test
    fun WHEN_different_priority_tasks_queued_THEN_correct_order_of_execution_happened() {
        // TODO: Test for various suspendedTime, to proof that it matters when suspendedTime is various
        val firstTask = BasicTask(Priority.CRITICAL, "1")
        val secondTask = BasicTask(Priority.HIGH, "2")
        val thirdTask = BasicTask(Priority.HIGH, "3")
        val fourthTask = BasicTask(Priority.HIGH, "4")
        val fifthTask = BasicTask(Priority.LOW, "5")

        val expectedTerminationOrder = listOf<Task>(
            firstTask, thirdTask, fourthTask, secondTask, fifthTask
        )
        run(initialTasks = expectedTerminationOrder)
        assertEquals(expected = expectedTerminationOrder, actual = system.terminatedTasks)
    }

    @Test
    fun WHEN_higher_priority_task_queued_THEN_current_executed_task_goes_to_queue() {
        val expectedTerminationOrder = listOf<Task>(
            BasicTask(Priority.CRITICAL, "2", suspendingTime = 50),
            BasicTask(Priority.HIGH, "1", executionTime = 100, suspendingTime = 10)
        )
        run(initialTasks = expectedTerminationOrder)
        assertEquals(expected = expectedTerminationOrder, actual = system.terminatedTasks)
    }


    @Test
    fun START_various_tasks_THEN_system_executes_them_in_correct_order_medium_case() {
        val expectedTerminationOrder = listOf<Task>(
            BasicTask(name = "4", priority = Priority.CRITICAL, suspendingTime = 80), // 80
            BasicTask(name = "3", priority = Priority.HIGH, suspendingTime = 60), // 60
            BasicTask(name = "2", priority = Priority.MEDIUM, suspendingTime = 40),
            BasicTask(name = "1", priority = Priority.LOW, suspendingTime = 20),
        )
        run(initialTasks = expectedTerminationOrder)
        assertEquals(expected = expectedTerminationOrder, actual = system.terminatedTasks)
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
}