import task.BasicTask
import task.State
import task.Task
import java.text.SimpleDateFormat
import java.util.Date

class LogService {

    private val formatter = SimpleDateFormat("HH:mm:ss.SSS")

    private val entryLength = 80
    private val intervalLength = 5

    private val tasksInterval = 4
    private val systemInterval = tasksInterval + entryLength + intervalLength
    private val schedulerInterval = tasksInterval + 2 * entryLength + 2 * intervalLength
    private val processorInterval = tasksInterval + 3 * entryLength + 3 * intervalLength

    fun taskChangedState(task: Task, startState: State, finishState: State) {
        println(
            String.format("${getCurrentTime()}%-${tasksInterval}s${("${Tags.TASK}: " +
                    "${getTaskShortInfo(task)} changed state ($startState -> $finishState)").padEnd(entryLength)}", " "
            )
        )
    }

    fun taskErrorWhileChangingState(task: Task, expectedState: State, actualState: State) {
        println(
            String.format("${getCurrentTime()}%-${tasksInterval}s${("${Tags.TASK}: " +
                    "error while changing state of ${getTaskShortInfo(task)}: expected - $expectedState, actual - $actualState").padEnd(entryLength)}", " "
            )
        )
    }

    fun systemInit(suspendedTasksSize: Int) {
        println(
            String.format("TIME" +
                "%-12sTASKS INFO" +
                "%-${entryLength+intervalLength-10}sSYSTEM THREAD" +
                "%-${entryLength+intervalLength-13}sSCHEDULER THREAD" +
                "%-${entryLength+intervalLength-16}sPROCESSOR THREAD", " ", " ", " ", " "
            )
        )
        println(
            String.format("${getCurrentTime()}%-${systemInterval}s${("${Tags.SYSTEM}: " +
                    "initialized, suspendedTasks size is $suspendedTasksSize").padEnd(entryLength)}", " "
            )
        )
    }

    fun systemActivatedTask(task: Task, suspendedTasksSize: Int) {
        println(
            String.format("${getCurrentTime()}%-${systemInterval}s${("${Tags.SYSTEM}: " +
                    "${getTaskShortInfo(task)} is activated, suspendedTasks size is $suspendedTasksSize").padEnd(entryLength)}", " "
            )
        )
    }

    fun systemTaskPushedToQueue(task: Task) {
        println(
            String.format("${getCurrentTime()}%-${systemInterval}s${("${Tags.QUEUE}: " +
                    "pushed ${getTaskShortInfo(task)}").padEnd(entryLength)}", " "
            )
        )
    }

    fun systemTermination() {
        println(
            String.format("${getCurrentTime()}%-${systemInterval}s${("${Tags.SYSTEM}: " +
                    "was terminated").padEnd(entryLength)}", " "
            )
        )
    }

    fun systemTerminationDelay() {
        println(
            String.format("${getCurrentTime()}%-${systemInterval}s${("${Tags.SYSTEM}: " +
                    "suspendedTasks.isEmpty() && queue.size < 1 , await termination delay").padEnd(entryLength)}", " "
            )
        )
    }

    fun schedulerTaskPoppedFromQueue(task: Task) {
        println(
            String.format("${getCurrentTime()}%-${schedulerInterval}s${("${Tags.QUEUE}: " +
                    "popped ${getTaskShortInfo(task)}").padEnd(intervalLength)}", " "
            )
        )
    }

    fun schedulerPoppedTaskWithHigherPriority(task: Task) {
        println(
            String.format("${getCurrentTime()}%-${schedulerInterval}s${("${Tags.SCHEDULER}: " +
                    "popped task with higher priority - ${getTaskShortInfo(task)}").padEnd(intervalLength)}", " "
            )
        )
    }

    fun schedulerStoppingProcessingTask(task: Task) {
        println(
            String.format("${getCurrentTime()}%-${schedulerInterval}s${("${Tags.SCHEDULER}: " +
                    "stopping processing task ${getTaskShortInfo(task)}").padEnd(intervalLength)}", " "
            )
        )
    }

    fun schedulerCurrentChoice(task: Task) {
        println(
            String.format("${getCurrentTime()}%-${schedulerInterval}s${("${Tags.SCHEDULER}: " +
                    "current choice is ${getTaskShortInfo(task)}").padEnd(intervalLength)}", " "
            )
        )
    }

    fun processorStartOfTaskExecution(task: Task) {
        println(
            String.format("${getCurrentTime()}%-${processorInterval}s${("${Tags.PROCESSOR}: " +
                    "execute ${getTaskShortInfo(task)}").padEnd(intervalLength)}", " "
            )
        )
    }

    fun processorFinishOfTaskExecution(task: Task) {
        println(
            String.format("${getCurrentTime()}%-${processorInterval}s${("${Tags.PROCESSOR}: " +
                    "finish of ${getTaskShortInfo(task)} execution").padEnd(intervalLength)}", " "
            )
        )
    }

    fun processorErrorWhileTaskExecution(task: Task) {
        println(
            String.format("${getCurrentTime()}%-${processorInterval}s${("${Tags.PROCESSOR}: " +
                    "error while ${getTaskShortInfo(task)} execution").padEnd(intervalLength)}", " "
            )
        )
    }

    fun processorThreadInitialization() {
        println(
            String.format("${getCurrentTime()}%-${processorInterval}s${("${Tags.PROCESSOR}: " +
                    "thread was initialized").padEnd(intervalLength)}", " "
            )
        )
    }

    fun processorThreadShutdown() {
        println(
            String.format("${getCurrentTime()}%-${processorInterval}s${("${Tags.PROCESSOR}: " +
                    "thread was shutdowned").padEnd(intervalLength)}", " "
            )
        )
    }

    fun processorThreadInterruption() {
        println(
            String.format("${getCurrentTime()}%-${processorInterval}s${("${Tags.PROCESSOR}: " +
                    "thread was interrupted").padEnd(intervalLength)}", " "
            )
        )
    }


    private fun getCurrentTime(): String {
        val date = Date()
        return formatter.format(date)
    }

    private fun getTaskShortInfo(task: Task): String =
        if (task is BasicTask) {
            "BasicTask(name=${task.name}, priority=${task.priority})"
        } else {
            "ExtendedTask(name=${task.name}, priority=${task.priority}))"
        }

    private fun getTaskFullInfo(task: Task): String =
        if (task is BasicTask) {
            "BasicTask(${task.name}, ${task.state}, ${task.priority})"
        } else {
            "ExtendedTask(${task.name}, ${task.state}, ${task.priority})"
        }

    private enum class Tags {
        TASK, SYSTEM, QUEUE, SCHEDULER, PROCESSOR
    }

}
