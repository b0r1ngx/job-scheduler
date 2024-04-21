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
    fun START_two_same_priority_tasks_THEN_system_execute_its_orderly() {
        val expectedTerminationOrder = listOf<Task>(
            BasicTask(suspendingTime = 20), BasicTask(suspendingTime = 20),
        )
        run(initialTasks = expectedTerminationOrder)
        assertEquals(expected = expectedTerminationOrder, actual = system.terminatedTasks)
    }

    @Test
    fun START_various_tasks_THEN_system_executes_them_in_correct_order() {
        // before first task execution is end, highest task appeared in queue
        val expectedTerminationOrder = listOf<Task>(
            BasicTask(name = "1", priority = Priority.LOW, suspendingTime = 20),
            BasicTask(name = "2", priority = Priority.MEDIUM, suspendingTime = 40),
            BasicTask(name = "3", priority = Priority.HIGH, suspendingTime = 60),
            BasicTask(name = "4", priority = Priority.CRITICAL, suspendingTime = 80)
        )
        run(initialTasks = expectedTerminationOrder)
        assertEquals(expected = expectedTerminationOrder, actual = system.terminatedTasks)
    }

    @Test
    fun WHEN_different_priority_tasks_queued_THEN_correct_order_of_execution_happened() {
        // this test includes WHEN_different_priority_tasks_queued_THEN_highest_priority_task_picked
        // TODO: Test for various suspendedTime, to proof that it matters when suspendedTime is various
        val firstTask = BasicTask(Priority.CRITICAL, "1")
        val secondTask = BasicTask(Priority.HIGH, "2")
        val thirdTask = BasicTask(Priority.HIGH, "3")
        val fourthTask = BasicTask(Priority.HIGH, "4")
        val fifthTask = BasicTask(Priority.LOW, "5")

        val expectedTerminationOrder: List<Task> = listOf(
            firstTask, thirdTask, fourthTask, secondTask, fifthTask
        )
        run(initialTasks = expectedTerminationOrder)
        assertEquals(expected = expectedTerminationOrder, actual = system.terminatedTasks)
    }

    @Test
    fun WHEN_higher_priority_task_queued_THEN_current_executed_task_goes_to_queue() {
        // before first task execution is end, highest task appeared in queue
        val firstTaskSuspendingTime = 10L
        val secondTaskSuspendingTime = 50L
        val firstTask = BasicTask(Priority.HIGH, "1", executionTime = 100, suspendingTime = firstTaskSuspendingTime)
        val secondTask = BasicTask(Priority.CRITICAL, "2", suspendingTime = secondTaskSuspendingTime)

        val expectedTerminationOrder: List<Task> = listOf(secondTask, firstTask)
        run(initialTasks = expectedTerminationOrder)
        assertEquals(expected = expectedTerminationOrder, actual = system.terminatedTasks)
    }
}