package IspitniZadaci;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class StudentRecordsTest {
    public static void main(String[] args) throws IOException {
        System.out.println("=== READING RECORDS ===");
        StudentRecords studentRecords = new StudentRecords();
        int total = studentRecords.readRecords(System.in);
        System.out.printf("Total records: %d\n", total);
        System.out.println("=== WRITING TABLE ===");
        studentRecords.writeTable(System.out);
        System.out.println("=== WRITING DISTRIBUTION ===");
        studentRecords.writeDistribution(System.out);
    }
}

class Record implements Comparable<Record>
{
    private String code;
    private String major;
    private List<Integer> grades;

    public Record(String code, String major, List<Integer> grades) {
        this.code = code;
        this.major = major;
        this.grades = grades;
    }
    public double average()
    {
        return grades.stream().mapToDouble(i->i).average().getAsDouble();
    }

    @Override
    public String toString() {
        return String.format("%s %.2f",code,average());
    }

    public String getCode() {
        return code;
    }

    public String getMajor() {
        return major;
    }

    public List<Integer> getGrades() {
        return grades;
    }

    @Override
    public int compareTo(Record o) {
        return Comparator.comparing(Record::average)
                .reversed()
                .thenComparing(Record::getCode).compare(this,o);
    }
}
class StudentRecords
{
    Map<String,Set<Record>> recordsByMajor;
    Map<String,List<Integer>> gradesByMajor;

    public StudentRecords()
    {
        this.recordsByMajor = new HashMap<>();
        this.gradesByMajor = new HashMap<>();
    }

    public int readRecords(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        int recordCount = 0;
        while ((line = br.readLine())!=null)
        {
            String [] parts = line.split("\\s+");
            String code = parts[0];
            String major = parts[1];
            List<Integer> grades = new ArrayList<>();
            for (int i=2;i<parts.length;i++)
            {
                grades.add(Integer.parseInt(parts[i]));
            }
            Record record = new Record(code,major,grades);
            recordsByMajor.putIfAbsent(major,new TreeSet<>());
            recordsByMajor.get(major).add(record);
            gradesByMajor.putIfAbsent(major,new ArrayList<>());
            gradesByMajor.get(major).addAll(grades);
            recordCount++;
        }
        return recordCount;
    }

    public void writeTable(OutputStream out) {
        String result = recordsByMajor.keySet().stream()
                .map(major -> {
                    String record = recordsByMajor.get(major)
                            .stream().map(Record::toString)
                            .collect(Collectors.joining("\n"));
                    return major + "\n" + record;
                }).collect(Collectors.joining("\n"));
        System.out.println(result);
    }

    public void writeDistribution(PrintStream out) {
        Comparator<Map.Entry<String,List<Integer>>> comparator =
                (o1, o2) -> {
                    int first = (int) o1.getValue().stream().filter(num -> num==10).count();
                    int second = (int) o2.getValue().stream().filter(num -> num==10).count();
                    return Integer.compare(second,first);
                };
        gradesByMajor.entrySet().stream().sorted(comparator)
                .forEach(entry -> {
                    System.out.println(entry.getKey());
                    List<Integer> grades = entry.getValue();
                    for (int i=6;i<=10;i++)
                    {
                        int finalI = i;
                        int count = (int) grades.stream().filter(num->num==finalI).count();
                        System.out.printf("%2d | %s(%d)\n",i,getStars(count),count);
                    }
                });
    }
    public String getStars(int count)
    {
        double end = Math.ceil(count/10.0);
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<end;i++)
        {
            sb.append("*");
        }
        return sb.toString();
    }
}


/*class Record implements Comparable<Record>
{
    private String code;
    private String smer;
    private List<Integer> grades;

    public Record(String code, String smer, List<Integer> grades) {
        this.code = code;
        this.smer = smer;
        this.grades = grades;
    }

    public String getCode() {
        return code;
    }

    public String getSmer() {
        return smer;
    }

    public List<Integer> getGrades() {
        return grades;
    }

    public double averageGrade()
    {
        return grades.stream().mapToDouble(g->g).average().getAsDouble();
    }

    @Override
    public String toString() {
        return String.format("%s %.2f",code,averageGrade());
    }

    @Override
    public int compareTo(Record o) {
        return Comparator.comparing(Record::averageGrade)
                .reversed().thenComparing(Record::getCode)
                .compare(this,o);
    }
}

class StudentRecords
{
    Map<String,Set<Record>> recordsBySmer;
    Map<String,List<Integer>> gradesBySmer;
    int recordsNumber;

    public StudentRecords()
    {
        this.recordsBySmer = new TreeMap<>();
        this.gradesBySmer = new TreeMap<>();
        this.recordsNumber = 0;
    }

    public int readRecords(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = br.readLine())!=null)
        {
            recordsNumber++;
            String [] parts = line.split("\\s+");
            String code = parts[0];
            String smer = parts[1];
            List<Integer> grades = new ArrayList<>();
            for (int i=2;i<parts.length;i++)
            {
                grades.add(Integer.parseInt(parts[i]));
            }
            Record record = new Record(code,smer,grades);
            recordsBySmer.putIfAbsent(smer,new TreeSet<>());
            recordsBySmer.get(smer).add(record);
            gradesBySmer.putIfAbsent(smer,new ArrayList<>());
            gradesBySmer.get(smer).addAll(grades);
        }
        return recordsNumber;
    }

    public void writeTable(PrintStream out) {
        String result = recordsBySmer.keySet().stream()
                .map(smer -> {
                    String record = recordsBySmer.get(smer)
                            .stream().map(Record::toString)
                            .collect(Collectors.joining("\n"));
                    return smer + "\n" + record;
                })
                .collect(Collectors.joining("\n"));
                System.out.println(result);
    }

    public void writeDistribution(PrintStream out) {
        /*List<Map.Entry<String,List<Integer>>> sortByValue
                = new LinkedList<>(gradesBySmer.entrySet());
        Collections.sort(sortByValue, new Comparator<Map.Entry<String, List<Integer>>>() {
            @Override
            public int compare(Map.Entry<String, List<Integer>> o1, Map.Entry<String, List<Integer>> o2) {
                int first = (int) o1.getValue().stream().filter(num->num == 10).count();
                int second = (int) o2.getValue().stream().filter(num -> num == 10).count();
                return Integer.compare(second,first);
            }
        });

        for (Map.Entry<String,List<Integer>> map: sortByValue)
        {
            gradesBySmer.put(map.getKey(),map.getValue());
        }*/

        /*Comparator<Map.Entry<String,List<Integer>>> comparator =
                (map1,map2) -> {
                    int first = (int) map1.getValue().stream().filter(num->num == 10).count();
                    int second = (int) map2.getValue().stream().filter(num -> num == 10).count();
                    return Integer.compare(second,first);
                };

        gradesBySmer.entrySet().stream().sorted(comparator).forEach(entry->{
                    System.out.println(entry.getKey());
                    List<Integer> grades = entry.getValue();
                    for (int i=6;i<=10;i++)
                    {
                        int finalI = i;
                        int count = (int) grades.stream().filter(num->num==finalI).count();
                        System.out.printf("%2d | %s(%d)\n",i,getStars(count),count);
                    }
                });
    }
    public String getStars(int count)
    {
        double end = Math.ceil(count/10.0);
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<end;i++)
        {
            sb.append("*");
        }
        return sb.toString();
    }
}

public class StudentRecordsTest {
    public static void main(String[] args) throws IOException {
        System.out.println("=== READING RECORDS ===");
        StudentRecords studentRecords = new StudentRecords();
        int total = studentRecords.readRecords(System.in);
        System.out.printf("Total records: %d\n", total);
        System.out.println("=== WRITING TABLE ===");
        studentRecords.writeTable(System.out);
        System.out.println("=== WRITING DISTRIBUTION ===");
        studentRecords.writeDistribution(System.out);
    }
}*/


