package IspitniZadaci;
import java.util.*;
import java.util.stream.Collectors;

class Student
{
    private String index;
    private List<Integer> points;

    public Student(String index, List<Integer> points) {
        this.index = index;
        this.points = points;
    }

    public String getIndex() {
        return index;
    }

    public boolean getSignature()
    {
        return points.size()>=8;
    }

    public List<Integer> getPoints() {
        return points;
    }
    public double averagePoints()
    {
        return points.stream().mapToDouble(point->point).sum()/10;
    }

    @Override
    public String toString() {
        return String.format("%s %s %.2f",index,getSignature()? "YES":"NO",averagePoints());
    }
}
class LabExercises
{
    Set<Student> students;

    public LabExercises()
    {
        this.students = new HashSet<>();
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void printByAveragePoints(boolean ascending, int n) {
       Comparator<Student> comparatorAscending =
               Comparator.comparing(Student::averagePoints)
                       .thenComparing(Student::getIndex);
       Comparator<Student> comparator;
       if (ascending)
       {
           comparator = comparatorAscending;
       }
       else {
           comparator = comparatorAscending.reversed();
       }
       students.stream()
               .sorted(comparator)
               .limit(n)
               .forEach(System.out::println);
    }

    public List<Student> failedStudents() {
        return students.stream()
                .filter(student -> !student.getSignature())
                .sorted(Comparator.comparing(Student::getIndex)
                        .thenComparing(Student::averagePoints))
                .collect(Collectors.toList());
    }

    public Map<Integer, Double> getStatisticsByYear() {
        Map<Integer,Double> sum = new TreeMap<>();
        Map<Integer,Integer> homManyStudents = new TreeMap<>();

        students.stream().filter(Student::getSignature)
                .forEach(student -> {
                    int year = 20 - Integer.parseInt(student.getIndex().substring(0,2));
                    sum.putIfAbsent(year,0.0);
                    sum.put(year,sum.get(year) + student.averagePoints());

                    homManyStudents.putIfAbsent(year,0);
                    homManyStudents.put(year,homManyStudents.get(year) + 1);
                });

        Map<Integer,Double> result = new TreeMap<>();
        sum.keySet().forEach(
                year->{
                    result.put(year,sum.get(year)/homManyStudents.get(year));
                }
        );
        return result;
    }
}

public class LabExercisesTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LabExercises labExercises = new LabExercises();
        while (sc.hasNextLine()) {
            String input = sc.nextLine();
            String[] parts = input.split("\\s+");
            String index = parts[0];
            List<Integer> points = Arrays.stream(parts).skip(1)
                    .mapToInt(Integer::parseInt)
                    .boxed()
                    .collect(Collectors.toList());

            labExercises.addStudent(new Student(index, points));
        }

        System.out.println("===printByAveragePoints (ascending)===");
        labExercises.printByAveragePoints(true, 100);
        System.out.println("===printByAveragePoints (descending)===");
        labExercises.printByAveragePoints(false, 100);
        System.out.println("===failed students===");
        labExercises.failedStudents().forEach(System.out::println);
        System.out.println("===statistics by year");
        labExercises.getStatisticsByYear().entrySet().stream()
                .map(entry -> String.format("%d : %.2f", entry.getKey(), entry.getValue()))
                .forEach(System.out::println);

    }
}
