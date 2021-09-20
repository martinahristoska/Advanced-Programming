package IspitniZadaci;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

abstract class Employee implements Comparable<Employee>
{
    private String id;
    private String level;
    private Double rate;

    public Employee(String id, String level, Double rate) {
        this.id = id;
        this.level = level;
        this.rate = rate;
    }

    public abstract double getSalary();

    @Override
    public String toString() {
        return String.format("Employee ID: %s Level: %s Salary: %.2f",
                id,level,getSalary());
    }

    public String getId() {
        return id;
    }

    public String getLevel() {
        return level;
    }

    public Double getRate() {
        return rate;
    }

    @Override
    public int compareTo(Employee o) {
        return Comparator.comparing(Employee::getSalary).reversed()
                .thenComparing(Employee::getLevel).compare(this,o);
    }

    public static Employee createEmployee(String line,Map<String, Double> hourlyRateByLevel, Map<String, Double> ticketRateByLevel)
    {
        String [] parts = line.split(";");
        String id = parts[1];
        String level = parts[2];
        if (parts[0].equals("H"))
        {
            double hours = Double.parseDouble(parts[3]);
            return new HourlyEmployee(id,level,hourlyRateByLevel.get(level),hours);
        }
        else {
            List<Integer> points = Arrays.stream(parts)
                    .skip(3).map(Integer::parseInt).collect(Collectors.toList());
            return new FreelanceEmployee(id,level,ticketRateByLevel.get(level),points);

        }
    }
}
class HourlyEmployee extends Employee
{
    double hours;
    double regular;
    double overtime;


    public HourlyEmployee(String id, String level, Double rate,double hours) {
        super(id, level, rate);
        this.hours = hours;
        this.overtime = Math.max(0,hours-40);
        this.regular = hours - overtime;
    }



    @Override
    public double getSalary() {
        return regular*getRate() + overtime*getRate()*1.5;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" Regular hours: %.2f Overtime hours: %.2f",
                regular,overtime);
    }
}
class FreelanceEmployee extends Employee
{
    List<Integer> ticketPoints;

    public FreelanceEmployee(String id, String level, Double rate,List<Integer> ticketPoints) {
        super(id, level, rate);
        this.ticketPoints = ticketPoints;
    }


    @Override
    public double getSalary() {
        return ticketPoints.stream().mapToInt(i->i).sum()*getRate();
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" Tickets count: %d Tickets points: %d",
                ticketPoints.size(),ticketPoints.stream().mapToInt(i->i).sum());
    }
}
class PayrollSystem
{
    Map<String,Double> hourlyRateByLevel;
    Map<String,Double> ticketRateByLevel;
    List<Employee> employees;

    public PayrollSystem(Map<String, Double> hourlyRateByLevel, Map<String, Double> ticketRateByLevel) {
        this.hourlyRateByLevel = hourlyRateByLevel;
        this.ticketRateByLevel = ticketRateByLevel;
    }

    public void readEmployees(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        employees = br.lines().map(line-> Employee.createEmployee(line,hourlyRateByLevel,ticketRateByLevel))
                .collect(Collectors.toList());
    }

    public Map<String, Set<Employee>> printEmployeesByLevels(Set<String> levels) {
         Map<String,Set<Employee>> mapEmployee = employees.stream()
                 .collect(Collectors.groupingBy(
                         Employee::getLevel,
                         TreeMap::new,
                         Collectors.toCollection(TreeSet::new)
                 ));
         Set<String> keys = new HashSet<>(mapEmployee.keySet());
         keys.stream().filter(k->!levels.contains(k))
                 .forEach(mapEmployee::remove);
         return mapEmployee;
    }
}


public class PayrollSystemTest {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 10 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5 + i * 2.5);
        }

        PayrollSystem payrollSystem = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);

        System.out.println("READING OF THE EMPLOYEES DATA");
        payrollSystem.readEmployees(System.in);

        System.out.println("PRINTING EMPLOYEES BY LEVEL");
        Set<String> levels = new LinkedHashSet<>();
        for (int i=5;i<=10;i++) {
            levels.add("level"+i);
        }
        Map<String, Set<Employee>> result = payrollSystem.printEmployeesByLevels(levels);
        result.forEach((level, employees) -> {
            System.out.println("LEVEL: "+ level);
            System.out.println("Employees: ");
            employees.forEach(System.out::println);
            System.out.println("------------");
        });


    }
}
