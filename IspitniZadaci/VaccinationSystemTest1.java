package IspitniZadaci;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

class  InvalidApplicationException extends Exception
{
    public  InvalidApplicationException(String name, String surname, String ID)
    {
        super(String.format("The application for %s %s with ID: %s is not allowed," +
                "because the citizen is under 18 years old.",name,surname,ID));
    }
}

abstract class Application implements Comparable<Application>
{
    private String ID;
    private String name;
    private String surname;
    private String profession;
    private LinkedList<String> vaccines;
    private Integer priority;

    public Application(String ID, String name, String surname, String profession, String vaccine1,String vaccine2,String vaccine3,Integer priority) {
        this.ID = ID;
        this.name = name;
        this.surname = surname;
        this.profession = profession;
        this.vaccines = new LinkedList<>();
        this.vaccines.add(vaccine1);
        this.vaccines.add(vaccine2);
        this.vaccines.add(vaccine3);
        this.priority = priority;
    }

    public String getID() {
        return ID;
    }

    public String getSurname() {
        return surname;
    }

    public String getProfession() {
        return profession;
    }

    public LinkedList<String> getVaccines() {
        return vaccines;
    }

    public String getName() {
        return name;
    }

    public Integer getPriority() {
        return priority;
    }

    public  static Application createPerson(String line) throws InvalidApplicationException {
        String [] parts = line.split("\\s+");
        String ID = parts[0];
        String name = parts[1];
        String surname = parts[2];
        String profession = parts[3];
        LinkedList<String> vaccines = new LinkedList<>();
        for (int i=4;i<parts.length;i++)
        {
            vaccines.add(parts[i]);
        }
        if (getAge(ID) < 18)
        {
            throw new InvalidApplicationException(name,surname,ID);
        }
        if (profession.equals("DOCTOR") || profession.equals("NURSE") || profession.equals("POLICEMAN") || profession.equals("FIREMAN"))
        {
            return new PriorityApplication(ID,name,surname,profession,vaccines.get(0),vaccines.get(1),vaccines.get(2));
        }
        else {
            return new BasicApplication(ID,name,surname,profession,vaccines.get(0),vaccines.get(1),vaccines.get(2));
        }
    }
    public static int getAge(String ID)
    {
        int day = Integer.parseInt(ID.substring(0,2));
        int month = Integer.parseInt(ID.substring(2,4));
        String year = ID.substring(4,7);
        char numYear = ID.charAt(4);
        if (numYear == '9')
        {
            year = "1" + year;
        }
        else
        {
            year = "2" + year;
        }
        int fullYear = Integer.parseInt(year);
        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.of(fullYear,month,day);
        int age = Period.between(birthday,today).getYears();
        return age;
    }
    private int getAge()
    {
        return getAge(this.ID);
    }
    public abstract String getType();

    @Override
    public String toString() {
        return String.format("%s %s %s %d %s %s %s %s",ID,name,surname,getAge(ID),getType()
                ,vaccines.get(0),vaccines.get(1),vaccines.get(2));
    }


    @Override
    public int compareTo(Application o) {
        return Comparator.comparing(Application::getPriority)
                .reversed()
                .thenComparing(Application::getAge)
                .reversed()
                .thenComparing(Application::getName)
                .compare(this,o);
    }
}
class PriorityApplication extends Application
{

    public PriorityApplication(String ID, String name, String surname, String profession, String vaccine1, String vaccine2, String vaccine3) {
        super(ID, name, surname, profession, vaccine1, vaccine2, vaccine3,1);
    }

    @Override
    public String getType() {
        return "PRIORITY";
    }
}
class BasicApplication extends Application
{

    public BasicApplication(String ID, String name, String surname, String profession, String vaccine1, String vaccine2, String vaccine3) {
        super(ID, name, surname, profession, vaccine1, vaccine2, vaccine3,2);
    }

    @Override
    public String getType() {
        return "BASIC";
    }
}

class VaccinationSystem
{
    List<Application> applications;

    public VaccinationSystem()
    {
        this.applications = new ArrayList<>();
    }

    public void readApplications(InputStream inputStream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        this.applications = br.lines().map(line -> {
            try {
                return Application.createPerson(line);
            } catch (InvalidApplicationException e) {
                System.out.println(e.getMessage());
                return null;
            }
        })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void printApplications(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);
        applications.stream().sorted().forEach(pw::println);
        pw.flush();
        pw.close();
    }
}

public class VaccinationSystemTest1 {

    public static void main(String[] args) {
        VaccinationSystem vaccinationSystem = new VaccinationSystem();

        System.out.println("--- READING FROM INPUT STREAM ---");
        vaccinationSystem.readApplications(System.in);

        System.out.println();
        System.out.println("--- PRINTING TO OUTPUT STREAM ---");
        vaccinationSystem.printApplications(System.out);
    }
}