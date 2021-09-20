package VtorKolokvium;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Event implements Comparable<Event>
{
    private String name;
    private String location;
    private Date date;

    public Event(String name, String location, Date date) {
        this.name = name;
        this.location = location;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public int getMonth()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        return month;
    }


    public Date getDate() {
        return date;
    }

    @Override
    public  int compareTo(Event o) {
        return Comparator.comparing(Event::getDate).thenComparing(Event::getName)
                .compare(this,o);
    }

    @Override
    public String toString() {
        DateFormat format = new SimpleDateFormat("dd MMM, YYY HH:mm");
        return String.format("%s at %s, %s",
                format.format(date),location,name);
    }
}

class EventCalendar
{
    int year;
    Map<Date, Set<Event>> events;
    Set<Event> eventSet;

    public EventCalendar(int year) {

        this.year = year;
        this.events = new HashMap<>();
        this.eventSet = new HashSet<>();
    }

    public void addEvent(String name, String location, Date date) throws WrongDateException {
        DateFormat df = new SimpleDateFormat("yyyy MMM dd");
        df.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year2 = calendar.get(Calendar.YEAR);
        if (this.year!= year2)
        {
            throw new WrongDateException(date);
        }
        events.putIfAbsent(date,new HashSet<>());
        events.get(date).add(new Event(name, location, date));
        eventSet.add(new Event(name, location, date));
    }

    public void listEvents(Date date) {
        if (events.get(date).isEmpty())
        {
            System.out.println("No events on this day");
        }
        else {
            events.get(date).stream()
                    .sorted(Event::compareTo)
                    .forEach(System.out::println);
        }
    }

    public void listByMonth() {
        Map<Integer,Long> map =
                eventSet.stream()
                .collect(Collectors.groupingBy(
                        Event::getMonth,
                        Collectors.counting()
                ));
        IntStream.range(0,12)
                .forEach(i->{
                    System.out.printf("%d : %d\n",i+1,map.containsKey(i)?
                            map.get(i).intValue():0);
                });
    }
}
class WrongDateException extends Exception
{
    public WrongDateException(Date date)
    {
        super("Wrong date: " + date);
    }
}

public class EventCalendarTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        int year = scanner.nextInt();
        scanner.nextLine();
        EventCalendar eventCalendar = new EventCalendar(year);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            String name = parts[0];
            String location = parts[1];
            Date date = df.parse(parts[2]);
            try {
                eventCalendar.addEvent(name, location, date);
            } catch (WrongDateException e) {
                System.out.println(e.getMessage());
            }
        }
        Date date = df.parse(scanner.nextLine());
        eventCalendar.listEvents(date);
        eventCalendar.listByMonth();
    }
}


