package PrvKolokvium;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

class Time implements Comparable<Time>
{
    private int hours;
    private int minutes;

    public Time(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    @Override
    public String toString() {
        return String.format("%2d:%02d",hours,minutes);
    }

    @Override
    public int compareTo(Time o) {
        if (this.hours == o.hours)
        {
            return Integer.compare(this.minutes,o.minutes);
        }
        return Integer.compare(this.hours,o.hours);
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
}
class TimeTable
{
    private List<Time> times;

    public TimeTable()
    {
        this.times = new ArrayList<>();
    }

    public void readTimes(InputStream in) throws UnsupportedFormatException, InvalidTimeException {
        Scanner sc = new Scanner(in);
        while (sc.hasNextLine())
        {
            String line = sc.nextLine();
            String [] parts = line.split("\\s+");
            for (int i=0;i<parts.length;i++)
            {
                if (!parts[i].contains(".") && !parts[i].contains(":"))
                {
                    throw new UnsupportedFormatException(parts[i]);
                }
                parts[i] = parts[i].replace(".",":");
                String [] splitParts = parts[i].split(":");
                int hours = Integer.parseInt(splitParts[0]);
                int minutes = Integer.parseInt(splitParts[1]);
                if (hours>23 || hours<0 || minutes>59 || minutes<0)
                {
                    throw new InvalidTimeException(parts[i]);
                }
                times.add(new Time(hours,minutes));
            }
        }
    }

    public void writeTimes(PrintStream out, TimeFormat format) {
        Collections.sort(times);
        if (format.equals(TimeFormat.FORMAT_AMPM))
        {
            times.forEach(time ->
            {
                if (time.getHours()==0)
                {
                    time.setHours(12);
                    System.out.println(time.toString() + " AM");
                }
                else if (time.getHours()>=1 && time.getHours()<12)
                {
                    System.out.println(time.toString() + " AM");
                }
                else if (time.getHours()==12)
                {
                    System.out.println(time.toString() + " PM");
                }
                else if (time.getHours()>=13 && time.getHours()<24)
                {
                    time.setHours(time.getHours()-12);
                    System.out.println(time.toString() + " PM");
                }
            });
        }
        else {
            times.forEach(System.out::println);
        }
    }
}
class UnsupportedFormatException extends Exception
{
    public UnsupportedFormatException(String message)
    {
        super(message);
    }
}
class InvalidTimeException extends Exception
{
    public InvalidTimeException(String message)
    {
        super(message);
    }
}

public class TimesTest {

    public static void main(String[] args) {
        TimeTable timeTable = new TimeTable();
        try {
            timeTable.readTimes(System.in);
        } catch (UnsupportedFormatException e) {
            System.out.println("UnsupportedFormatException: " + e.getMessage());
        } catch (InvalidTimeException e) {
            System.out.println("InvalidTimeException: " + e.getMessage());
        }
        System.out.println("24 HOUR FORMAT");
        timeTable.writeTimes(System.out, TimeFormat.FORMAT_24);
        System.out.println("AM/PM FORMAT");
        timeTable.writeTimes(System.out, TimeFormat.FORMAT_AMPM);
    }

}

enum TimeFormat {
    FORMAT_24, FORMAT_AMPM
}

