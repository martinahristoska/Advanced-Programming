package Lab7;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

class TermFrequency
{
      Map<String,Integer> words;
      int count;

      public TermFrequency()
      {
          this.words = new TreeMap<>();
          this.count = 0;
      }

      public TermFrequency(InputStream inputStream, String [] stopWords)
      {
          this();
          Scanner sc = new Scanner(inputStream);
          while (sc.hasNext())
          {
              String line = sc.next();
              line = line.toLowerCase().replace("."," ")
                      .replace(","," ").trim();
              if (!(line.isEmpty() || Arrays.asList(stopWords).contains(line)))
              {
                  int br = words.computeIfAbsent(line,k->0);
                  words.put(line,++br);
                  count++;
                  System.out.println(br);
              }
          }
      }

    public int countTotal() {
          return count;
    }

    public int countDistinct() {
          return words.size();
    }

    public List<String> mostOften(int k) {
          return words.keySet()
                  .stream().sorted(Comparator.comparing(key -> words.get(key)).reversed())
                  .limit(k).collect(Collectors.toList());
    }
}

public class TermFrequencyTest {
    public static void main(String[] args) throws FileNotFoundException {
        String[] stop = new String[] { "во", "и", "се", "за", "ќе", "да", "од",
                "ги", "е", "со", "не", "тоа", "кои", "до", "го", "или", "дека",
                "што", "на", "а", "но", "кој", "ја" };
        TermFrequency tf = new TermFrequency(System.in, stop);
        System.out.println(tf.countTotal());
        System.out.println(tf.countDistinct());
        System.out.println(tf.mostOften(10));
    }
}
