import model.Priority
import model.Scheduler
import model.Task
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
            assertEquals(task, scheduler.queue.take())
        }
    }

    @Test
    fun WHEN_different_priority_tasks_queued_THEN_highest_priority_task_picked() {
        val firstTask = Task(Priority.CRITICAL, "1")
        val secondTask = Task(Priority.HIGH, "2")
        val thirdTask = Task(Priority.HIGH, "3")
        val fourthTask = Task(Priority.HIGH, "4")
        val fifthTask = Task(Priority.LOW, "5")

        scheduler.addTask(thirdTask) // 3
        scheduler.addTask(firstTask)
        scheduler.addTask(fifthTask) // 5
        scheduler.addTask(fourthTask)
        scheduler.addTask(secondTask) // 2

        val expectedExecutionOrder = listOf(
            firstTask, thirdTask, fifthTask, secondTask, fourthTask
        )

        val actualExecutionOrder = scheduler.start()


        print("") // TODO: to Kotlin devs: if we comment this line, below prints doesn't executes
        actualExecutionOrder.forEach {
            println(it.name)
        }

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