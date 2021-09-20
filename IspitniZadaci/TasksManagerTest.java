package IspitniZadaci;
import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

interface ITask
{
    LocalDateTime getDeadline();
    Integer getPriority();
    String toString();

    default long getTimeLeft() {
        return Math.abs(Duration.between(LocalDateTime.now(), getDeadline()).getSeconds());
    }
}

class SimpleTask implements ITask
{
    String name;
    String description;

    public SimpleTask(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public LocalDateTime getDeadline() {
        return LocalDateTime.MAX;
    }

    @Override
    public Integer getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
abstract class TaskDecorator implements ITask
{
    ITask wrapperTask;

    public TaskDecorator(ITask wrapperTask)
    {
        this.wrapperTask = wrapperTask;
    }
}
class DeadlineDecorator extends TaskDecorator
{
    LocalDateTime deadline;

    public DeadlineDecorator(ITask wrapperTask,LocalDateTime deadline) {
        super(wrapperTask);
        this.deadline = deadline;
    }

    @Override
    public LocalDateTime getDeadline() {
        return deadline;
    }

    @Override
    public Integer getPriority() {
        return wrapperTask.getPriority();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( wrapperTask.toString().substring(0,wrapperTask.toString().length()-1));
        sb.append(", deadline=").append(deadline);
        sb.append("}");
        return sb.toString();
    }
}
class PriorityDecorator extends TaskDecorator
{
    Integer priority;

    public PriorityDecorator(ITask wrapperTask,Integer priority) {
        super(wrapperTask);
        this.priority = priority;
    }

    @Override
    public LocalDateTime getDeadline() {
        return wrapperTask.getDeadline();
    }

    @Override
    public Integer getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( wrapperTask.toString().substring(0,wrapperTask.toString().length()-1));
        sb.append(", priority=").append(priority);
        sb.append("}");
        return sb.toString();
    }
}
class DeadlineNotValidException extends Exception {
    public DeadlineNotValidException(LocalDateTime deadline) {
        super(String.format("The deadline %s has already passed", deadline));
    }
}
//School,NP,lab 1 po NP,2020-06-23T23:59:59.000,1
class TaskCreator
{
    public static ITask createTask(String line) throws DeadlineNotValidException {
        String [] parts = line.split(",");
        String name = parts[1];
        String description = parts[2];

        ITask simpleTask = new SimpleTask(name,description);

        if (parts.length == 3)
        {
            return simpleTask;
        }
        if (parts.length==4)
        {
            try {
                Integer priority = Integer.parseInt(parts[3]);
                return new PriorityDecorator(simpleTask,priority);
            }
            catch (Exception e)
            {
                LocalDateTime deadline = LocalDateTime.parse(parts[3]);
                return new DeadlineDecorator(simpleTask,deadline);
            }
        }
        else {
            LocalDateTime deadline = LocalDateTime.parse(parts[3]);
            checkDeadline(deadline);
            Integer priority = Integer.parseInt(parts[4]);
            return new PriorityDecorator(new DeadlineDecorator(simpleTask,deadline),priority);
        }
    }
    private static void checkDeadline(LocalDateTime deadline) throws DeadlineNotValidException {
        if (deadline.isBefore(LocalDateTime.now()))
            throw new DeadlineNotValidException(deadline);
    }
    public static String getCategory(String line)
    {
        return line.split(",")[0];
    }
}

class TaskManager
{

    Map<String, List<ITask>> tasksMap;

    TaskManager() {
        tasksMap = new TreeMap<>();
    }

    public void readTasks(InputStream in) {
        new BufferedReader(new InputStreamReader(in))
                .lines()
                .forEach(line -> {
                    String category = TaskCreator.getCategory(line);
                    try {
                        ITask task = TaskCreator.createTask(line);
                        tasksMap.putIfAbsent(category, new ArrayList<>());
                        tasksMap.computeIfPresent(category, (k, v) -> {
                                    v.add(task);
                                    return v;
                                }
                        );
                    } catch (DeadlineNotValidException e) {
                        System.out.println(e.getMessage());
                    }
                });
    }

    public void printTasks(OutputStream out, boolean includePriority, boolean includeCategory) {
        PrintWriter pw = new PrintWriter(out);

        Comparator<ITask> deadlineComparator = Comparator.comparing(ITask::getTimeLeft);
        Comparator<ITask> priorityAndDeadlineComparator = Comparator.comparing(ITask::getPriority).thenComparing(deadlineComparator);

        Comparator<ITask> taskComparator = includePriority ? priorityAndDeadlineComparator : deadlineComparator;
        if (includeCategory) {
            tasksMap.forEach((category, tasks) -> {
                pw.println(category.toUpperCase());
                tasks.stream().sorted(taskComparator).forEach(pw::println);
            });
        }
        else {
            tasksMap.values().stream()
                    .flatMap(Collection::stream)
                    .sorted(taskComparator)
                    .forEach(pw::println);
        }
        pw.flush();
    }
}


public class TasksManagerTest {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        System.out.println("Tasks reading");
        manager.readTasks(System.in);
        System.out.println("By categories with priority");
        manager.printTasks(System.out, true, true);
        System.out.println("-------------------------");
        System.out.println("By categories without priority");
        manager.printTasks(System.out, false, true);
        System.out.println("-------------------------");
        System.out.println("All tasks without priority");
        manager.printTasks(System.out, false, false);
        System.out.println("-------------------------");
        System.out.println("All tasks with priority");
        manager.printTasks(System.out, true, false);
        System.out.println("-------------------------");

    }
}

