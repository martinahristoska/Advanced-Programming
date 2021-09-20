package PrvKolokvium;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

abstract class Temperatures
{
    int value;

    public Temperatures(int value) {
        this.value = value;
    }
    public abstract double getCelsius();
    public abstract double getFahrenheit();

    public static Temperatures createTemperature(String line)
    {
        char type = line.charAt(line.length()-1);
        int value = Integer.parseInt(line.substring(0,line.length()-1));
        if (type=='C')
        {
            return new Celsius(value);
        }
        else return new Fahrenheit(value);
    }
}
class Celsius extends Temperatures
{

    public Celsius(int value) {
        super(value);
    }

    @Override
    public double getCelsius() {
        return value ;
    }

    @Override
    public double getFahrenheit() {
        return (value*9)/5.0 + 32;
    }
}
class Fahrenheit extends Temperatures
{

    public Fahrenheit(int value) {
        super(value);
    }

    @Override
    public double getCelsius() {
        return ((value-32)*5)/9.0;
    }

    @Override
    public double getFahrenheit() {
        return value;
    }
}

class DailyMeasurements implements Comparable<DailyMeasurements>
{
    private int day;
    private List<Temperatures> temperatures;

    public DailyMeasurements(int day, List<Temperatures> temperatures) {
        this.day = day;
        this.temperatures = temperatures;
    }

    @Override
    public int compareTo(DailyMeasurements o) {
        return Integer.compare(this.day,o.day);
    }

    public static DailyMeasurements createDailyMeasurements(String line)
    {
        String [] parts = line.split(" ");
        int day = Integer.parseInt(parts[0]);

        List<Temperatures> temperatures = Arrays.stream(parts).skip(1)
                .map(Temperatures::createTemperature).collect(Collectors.toList());
        return new DailyMeasurements(day,temperatures);
    }
    public String toString(char scale)
    {
        DoubleSummaryStatistics dbs =
                temperatures.stream().mapToDouble(t-> scale=='C' ? t.getCelsius() : t.getFahrenheit())
                .summaryStatistics();

        return String.format("%3d: Count: %3d Min: %6.2f%c Max: %6.2f%c Avg: %6.2f%c",
                day,
                dbs.getCount(),
                dbs.getMin(),
                scale,
                dbs.getMax(),
                scale,
                dbs.getAverage(),
                scale);
    }
}

class DailyTemperatures
{
    List<DailyMeasurements> dailyMeasurements;

    public DailyTemperatures() {
        this.dailyMeasurements = new ArrayList<>();
    }

    public void readTemperatures(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        dailyMeasurements = br.lines()
                .map(DailyMeasurements::createDailyMeasurements)
                .collect(Collectors.toList());
    }

    public void writeDailyStats(PrintStream out, char scale) {
        PrintWriter pw = new PrintWriter(out);
        Collections.sort(dailyMeasurements);
        dailyMeasurements.forEach(dm -> pw.println(dm.toString(scale)));
        pw.flush();
    }
}

public class DailyTemperatureTest {

    public static void main(String[] args) {
        DailyTemperatures dailyTemperatures = new DailyTemperatures();
        dailyTemperatures.readTemperatures(System.in);
        System.out.println("=== Daily temperatures in Celsius (C) ===");
        dailyTemperatures.writeDailyStats(System.out, 'C');
        System.out.println("=== Daily temperatures in Fahrenheit (F) ===");
        dailyTemperatures.writeDailyStats(System.out, 'F');
    }
}