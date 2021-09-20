package PrvKolokvium;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

class Pilot implements Comparable<Pilot>
{
    private String name;
    private String lap1;
    private String lap2;
    private String lap3;

    public Pilot(String name, String lap1, String lap2, String lap3) {
        this.name = name;
        this.lap1 = lap1;
        this.lap2 = lap2;
        this.lap3 = lap3;
    }
    public String bestLap()  {
        String best = lap1;
        if (lap2.compareTo(lap1)<0)
        {
            best = lap2;
        }
        if (lap3.compareTo(best)<0)
        {
            best = lap3;
        }
        return best;
    }

    @Override
    public int compareTo(Pilot o) {
        return bestLap().compareTo(o.bestLap());
    }

    @Override
    public String toString() {
            return String.format("%-10s%10s",name,bestLap());
    }
}
class F1Race
{
    private List<Pilot> pilots;

    public F1Race()
    {
        this.pilots = new ArrayList<>();
    }

    public void readResults(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = br.readLine()) != null)
        {
            String [] parts = line.split(" ");
            String name = parts[0];
            String lap1 = parts[1];
            String lap2 = parts[2];
            String lap3 = parts[3];
            this.pilots.add(new Pilot(name,lap1,lap2,lap3));
        }
    }

    public void printSorted(PrintStream out) {
        PrintWriter pw = new PrintWriter(out);
        Collections.sort(pilots);
        for (int i=0;i<pilots.size();i++)
        {
            pw.println(i+1 + ". " + pilots.get(i).toString());
        }
        pw.flush();
    }
}

public class F1Test {
    public static void main(String[] args) throws IOException {
        F1Race f1Race = new F1Race();
        f1Race.readResults(System.in);
        f1Race.printSorted(System.out);
    }
}
