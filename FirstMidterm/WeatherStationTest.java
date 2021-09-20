package PrvKolokvium;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

class Measurements implements Comparable<Measurements>
{
    private float temperature;
    private float wind;
    private float humidity;
    private float visibility;
    private Date date;

    public Measurements(float temperature, float wind, float humidity, float visibility, Date date) {
        this.temperature = temperature;
        this.wind = wind;
        this.humidity = humidity;
        this.visibility = visibility;
        this.date = date;
    }

    @Override
    public int compareTo(Measurements o) {
        return this.date.compareTo(o.date);
    }

    public Date getDate() {
        return date;
    }

    public float getTemperature() {
        return temperature;
    }

    @Override
    public String toString() {
        return String.format("%.1f %.1f km/h %.1f%% %.1f km %s",
                temperature,
                wind,
                humidity,
                visibility,
                date);
    }
}

class WeatherStation
{
    private int days;
    private List<Measurements> measurements;

    public WeatherStation(int days) {
        this.days = days;
        this.measurements = new ArrayList<>();
    }

    public void addMeasurment(float temperature, float wind, float humidity, float visibility, Date date) {
        Measurements newMeasurement = new Measurements(temperature, wind, humidity, visibility, date);

        for (Measurements measurement1 : measurements)
        {
            if (newMeasurement.getDate().getTime()-measurement1.getDate().getTime()<TimeUnit.MINUTES.toMillis((long) 2.5))
            {
                return;
            }
        }
        measurements.add(newMeasurement);
        measurements.removeIf(x->newMeasurement.getDate().getTime()-x.getDate().getTime()> TimeUnit.DAYS.toMillis(days));

    }

    public int total() {
        return measurements.size();
    }

    public void status(Date from, Date to) {
        Collections.sort(measurements);
        List<Measurements> fromTo;
        fromTo = measurements
                .stream()
                .filter(m->(m.getDate().after(from) || m.getDate().equals(from))
                && (m.getDate().before(to) || m.getDate().equals(to))).collect(Collectors.toList());
        if (fromTo.isEmpty())
        {
            throw new RuntimeException();
        }
        fromTo.forEach(System.out::println);
        double average = fromTo.stream().mapToDouble(Measurements::getTemperature).average().getAsDouble();
        System.out.println(String.format("Average temperature: %.2f",average));
    }
}

public class WeatherStationTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        int n = scanner.nextInt();
        scanner.nextLine();
        WeatherStation ws = new WeatherStation(n);
        while (true) {
            String line = scanner.nextLine();
            if (line.equals("=====")) {
                break;
            }
            String[] parts = line.split(" ");
            float temp = Float.parseFloat(parts[0]);
            float wind = Float.parseFloat(parts[1]);
            float hum = Float.parseFloat(parts[2]);
            float vis = Float.parseFloat(parts[3]);
            line = scanner.nextLine();
            Date date = df.parse(line);
            ws.addMeasurment(temp, wind, hum, vis, date);
        }
        String line = scanner.nextLine();
        Date from = df.parse(line);
        line = scanner.nextLine();
        Date to = df.parse(line);
        scanner.close();
        System.out.println(ws.total());
        try {
            ws.status(from, to);
        } catch (RuntimeException e) {
            System.out.println(e);
        }
    }
}

