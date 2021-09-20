package IspitniZadaci;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

class Subtitle
{
    private int number;
    private String startTime;
    private String endTime;
    private List<String> text;

    public Subtitle(int number, String startTime, String endTime, List<String> text) {
        this.number = number;
        this.startTime = startTime;
        this.endTime = endTime;
        this.text = text;
    }

    public int timeInMilliseconds(String time)
    {
        String [] parts = time.split(":");
        int hours = (int) TimeUnit.HOURS.toMillis(Integer.parseInt(parts[0]));
        int minutes = (int) TimeUnit.MINUTES.toMillis(Integer.parseInt(parts[1]));
        String [] parts2 = parts[2].split(",");
        int seconds = (int) TimeUnit.SECONDS.toMillis(Integer.parseInt(parts2[0]));
        int milliseconds = Integer.parseInt(parts2[1]);
        return hours+minutes+seconds+milliseconds;
    }

    public void shift(int ms)
    {
        int timeStartInMs = timeInMilliseconds(startTime);
        int timeEndInMs = timeInMilliseconds(endTime);
        this.startTime = timeToString(timeStartInMs + ms);
        this.endTime = timeToString(timeEndInMs + ms);

    }

    public String timeToString(int timeInMs)
    {
        /*long hours = TimeUnit.MILLISECONDS.toHours(timeInMs);
        long minutes = TimeUnit.MICROSECONDS.toMinutes(timeInMs);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMs);
        long milliseconds = TimeUnit.MILLISECONDS.toMillis(timeInMs);
        return String.format("%02d:%02d:%02d,%03d",hours,minutes,seconds,milliseconds);*/

        long hours = timeInMs/(60*60*1000);
        timeInMs%=(60*60*1000);
        long minutes = timeInMs/(60*1000);
        timeInMs%=(60*1000);
        long seconds = timeInMs/1000;
        long milliseconds = timeInMs%1000;
        return String.format("%02d:%02d:%02d,%03d",hours,minutes,seconds,milliseconds);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(number + "\n");
        sb.append(startTime + " --> " + endTime + "\n");
        text.forEach(t->sb.append(t + "\n"));
        return sb.toString();
    }
}

class Subtitles
{
    List<Subtitle> subtitles;

    public Subtitles()
    {
        this.subtitles = new ArrayList<>();
    }

    public int loadSubtitles(InputStream in) {
        Scanner sc = new Scanner(in);
        while (sc.hasNextLine())
        {
            int number = Integer.parseInt(sc.nextLine());
            String line = sc.nextLine();
            String [] parts = line.split("\\s+");
            String timeStart = parts[0];
            String endTIme = parts[2];
            List<String> text = new ArrayList<>();
            while (sc.hasNextLine())
            {
                String textLine = sc.nextLine();
                if (textLine.equals(""))
                {
                    break;
                }
                text.add(textLine);
            }
            subtitles.add(new Subtitle(number,timeStart,endTIme,text));
        }
        return subtitles.size();
    }

    public void print() {
        subtitles.forEach(System.out::println);
    }

    public void shift(int ms) {
        subtitles.forEach(s->s.shift(ms));
    }
}

public class SubtitlesTest {
    public static void main(String[] args)  {
        Subtitles subtitles = new Subtitles();
        int n = subtitles.loadSubtitles(System.in);
        System.out.println("+++++ ORIGINIAL SUBTITLES +++++");
        subtitles.print();
        int shift = n * 37;
        shift = (shift % 2 == 1) ? -shift : shift;
        System.out.println(String.format("SHIFT FOR %d ms", shift));
        subtitles.shift(shift);
        System.out.println("+++++ SHIFTED SUBTITLES +++++");
        subtitles.print();
    }
}

