import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Event implements Comparable<Event>
{
    private String eventName;
    private String location;
    private Date date;

    public Event(String eventName, String location, Date date) {
        this.eventName = eventName;
        this.location = location;
        this.date = date;
    }

    @Override
    public String toString() {
        DateFormat formatter = new SimpleDateFormat("dd MMM, YYY HH:mm");
        return String.format("%s at %s, %s",formatter.format(date),location,eventName);
    }

    public String getEventName() {
        return eventName;
    }
    public int month()
    {
        return date.getMonth();
    }
   

    public String getLocation() {
        return location;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public int compareTo(Event o) {
        return Comparator.comparing(Event::getDate).thenComparing(Event::getEventName).compare(this,o);
    }
}

class EventCalendar
{
    int year;
    Set<Event> events;

    public EventCalendar(int year) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        this.year = year-1900;
        this.events = new TreeSet<>();
    }

    public void addEvent(String name, String location, Date date) throws WrongDateException {
        if (this.year!=date.getYear())
            throw new  WrongDateException(date);
        events.add(new Event(name,location,date));
    }

    public void listEvents(Date date) {
        Predicate<Event> sameDate = event -> (event.getDate().getMonth() == date.getMonth())&&(event.getDate().getDate() == date.getDate());
        List<Event> list = events.stream().filter(sameDate).collect(Collectors.toList());
        if (list.isEmpty())
        {
            System.out.println("No events on this day!");
        }
        else {
            list.forEach(System.out::println);
        }
    }

    public void listByMonth() {
        Map<Integer,Long> map = events.stream()
                .collect(Collectors.groupingBy(
                        Event::month,
                        Collectors.counting()
                ));
        IntStream.range(0,12).forEach(i-> {
            System.out.printf("%d : %d\n",i+1,map.containsKey(i) ? map.get(i).intValue() : 0);
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

