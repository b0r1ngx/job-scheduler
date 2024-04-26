## Job scheduler

### Project overview

### Tests

- checking adding tasks to `System`,
- execution of the flow logic `System`,
- adding and taking tasks from `Queue`,
- adding tasks to the system and executing them in the correct order,
- tasks with the same priority,
- tasks with different priorities,
- tasks with different priorities and different times of transition to the `Ready` state (with and without intersection entering the queue),
- tasks with different priorities, mixing basic and extended tasks (+ extended tasks with switching to the `WAITING` state).

### Code coverage

To get detailed information about code coverage, use:
```bash
./gradlew koverHtmlReport
```

To just get percents, use:

```bash
./gradlew :koverLog
```

outputs:

```
> Task :koverPrintCoverage
application line coverage: 89.9654%
```
