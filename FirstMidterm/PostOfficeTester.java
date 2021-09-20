package PrvKolokvium;
import java.io.PrintStream;
import java.util.*;
import java.io.OutputStream;
import java.io.PrintWriter;

class InvalidPackageException extends RuntimeException{
    public InvalidPackageException(String message) {
        super(message);
    }
}

class PostOffice
{
   private String postName;
   private String location;
   private List<Package> packages;

    public PostOffice(String postName, String location) {
        this.postName = postName;
        this.location = location;
        this.packages = new ArrayList<>();
    }

    public String getPostName() {
        return postName;
    }

    public String getLocation() {
        return location;
    }

    public void addPackage(Package p) {
        packages.add(p);
    }

    public void printPackages(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        for (Package p: packages)
        {
            pw.print(p.format(""));
        }
        pw.flush();
    }

    public Package mostExpensive() {
        Collections.sort(packages);
        return packages.get(0);
    }

    public void loadPackages(Scanner scanner) {
        while (scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            String [] parts = line.split(", ");

            String type = parts[0];
            String name = parts[1];
            String address = parts[2];
            int trackingNumber = Integer.parseInt(parts[3]);
            int weight = Integer.parseInt(parts[4]);
            if (weight<=0)
            {
                throw new InvalidPackageException(name);
            }
            if (type.equalsIgnoreCase("I"))
            {
                packages.add(new InternationalPackage(name,address,trackingNumber,weight,parts[5]));
            }
            else if (type.equalsIgnoreCase("L"))
            {
                packages.add(new LocalPackage(name,address,trackingNumber,weight,Boolean.parseBoolean(parts[5])));
            }
            else throw new InvalidPackageException(name);

        }
    }
}
abstract class Package implements Comparable<Package>
{
    private String name;
    private String address;
    private int trackingNumber;
    int weight;

    public Package(String name, String address, int trackingNumber, int weight) {
        this.name = name;
        this.address = address;
        this.trackingNumber = trackingNumber;
        this.weight = weight;
    }
    abstract double getPrice();
    abstract double getWeight();
    abstract String getType();
    public abstract String format(String space);

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getTrackingNumber() {
        return trackingNumber;
    }

    @Override
    public int compareTo(Package o) {
        return Comparator.comparing(Package::getPrice)
                .reversed()
                .compare(this,o);
    }

    @Override
    public String toString() {
        return String.format("%s %s %d %d",name,address,trackingNumber,weight);
    }
}
class InternationalPackage extends Package
{
    String region;

    public InternationalPackage(String name, String address, int trackingNumber, int weight, String region) {
        super(name, address, trackingNumber, weight);
        this.region = region;
    }

    @Override
    double getPrice() {
        return getWeight()*1.5;
    }

    @Override
    double getWeight() {
        return weight;
    }

    @Override
    String getType() {
        return "I";
    }

    @Override
    public String format(String space) {
        return space + this.toString();
    }

    @Override
    public String toString() {
        return String.format("I, %s %s\n",super.toString(),region);
    }
}
class LocalPackage extends Package
{
    boolean priority;

    public LocalPackage(String name, String address, int trackingNumber, int weight, boolean priority) {
        super(name, address, trackingNumber, weight);
        this.priority = priority;
    }

    @Override
    double getPrice() {
        if (priority)
            return getWeight()*5;
        return getWeight()*3;
    }

    @Override
    double getWeight() {
        return weight;
    }

    @Override
    String getType() {
        return "L";
    }

    @Override
    public String format(String space) {
        return space + this.toString();
    }

    @Override
    public String toString() {
        return String.format("L %s %b\n",super.toString(),priority);
    }
}
class GroupPackage extends Package
{
    List<Package> groupPackages;

    public GroupPackage(String name, String address, int trackingNumber, int weight) {
        super(name, address, trackingNumber, weight);
        this.groupPackages = new ArrayList<>();
    }

    @Override
    double getPrice() {
        return groupPackages.stream().mapToDouble(Package::getPrice)
                .sum() + 2;
    }

    @Override
    double getWeight() {
        return groupPackages.stream().mapToDouble(Package::getWeight)
                .sum();
    }

    @Override
    String getType() {
        return "G";
    }

    @Override
    public String format(String space) {
        String s;
        s = space + this.toString();
        for (Package p : groupPackages)
        {
            s+=p.format(space + " ");
        }
        return s;
    }

    public boolean addPackage(Package p)
    {
        return groupPackages.add(p);
    }

    @Override
    public String toString() {
        return String.format("G %s",super.toString());
    }
}

public class PostOfficeTester {
    public static void main (String [] args) {
        Scanner scanner = new Scanner(System.in);
        int test = Integer.parseInt(scanner.nextLine());
        if(test == 1) {
            //Group Package printing
            System.out.println("======Packages======");
            PostOffice office = new PostOffice("Poshta", "Skopje");
            office.addPackage(new InternationalPackage("John_Doe", "Main_St_123", 111, 4, "America"));
            GroupPackage groupPackage = new GroupPackage("John_Done", "Main_St_123", 232, 0);
            groupPackage.addPackage(new LocalPackage("Jon_Snow", "The_Wall", 432, 5, true));
            office.addPackage(groupPackage);
            office.printPackages(System.out);
        }
        if(test == 2) {
            //Nested Group Package printing
            System.out.println("======Packages======");
            PostOffice office = new PostOffice("Poshta", "Skopje");
            office.addPackage(new InternationalPackage("Richard_Hendricks", "Noble_Pathway", 325, 4, "Africa"));
            office.addPackage(new LocalPackage("Jalisa_Acheson", "Emerald_Harbour", 600, 14, false));

            GroupPackage groupPackage = new GroupPackage("Meagan_Schuette", "Westeros", 232, 0);
            groupPackage.addPackage(new LocalPackage("Meagan_Schuette", "Westeros", 432, 5, true));
            groupPackage.addPackage(new InternationalPackage("Sansa_Stark", "Westeros", 332, 3, "Asia"));

            GroupPackage nestedGroupPackage = new GroupPackage("Marline_Bohling", "Crystal_Hills", 284, 0);
            nestedGroupPackage.addPackage(new InternationalPackage("Edie_Bramblett", "Lazy_Treasure", 382, 7, "Europe"));
            nestedGroupPackage.addPackage(new InternationalPackage("Cassaundra_Huff", "Sleepy_Farms", 696, 1, "Asia"));
            nestedGroupPackage.addPackage(new InternationalPackage("German_Sabbagh", "Tawny_Heath", 963, 12, "Africa"));

            groupPackage.addPackage(nestedGroupPackage);
            office.addPackage(groupPackage);
            office.addPackage(new LocalPackage("Clemmie_Reves", "Little_Cloud", 217, 5, true));
            office.printPackages(System.out);
        }
        if(test == 3) {
            //Most expensive Group Package
            System.out.println("======Packages======");
            PostOffice office = new PostOffice("Poshta", "Skopje");
            office.addPackage(new InternationalPackage("Dohn_Joe", "Main_St_321", 444, 4, "Europe"));
            GroupPackage groupPackage = new GroupPackage("John_Jon", "First_St_123", 232, 0);
            groupPackage.addPackage(new LocalPackage("Jon_Snow", "Westeros", 432, 5, true));
            groupPackage.addPackage(new InternationalPackage("Sansa_Stark", "Westeros", 332, 3, "Asia"));
            office.addPackage(groupPackage);
            office.addPackage(new LocalPackage("Littlefinger", "The_Eyrie", 987, 7, false));
            office.printPackages(System.out);
            System.out.println();
            System.out.println("======MostExpensive======");
            System.out.println(office.mostExpensive());
        }
        if(test == 4) {
            //Most expensive Local Package
            System.out.println("======Packages======");
            PostOffice office = new PostOffice("Poshta", "Skopje");
            office.addPackage(new InternationalPackage("Dohn_Joe", "Main_St_321", 444, 15, "Europe"));
            office.addPackage(new LocalPackage("Jon_Snow", "Westeros", 432, 5, true));
            office.addPackage(new InternationalPackage("Sansa_Stark", "Westeros", 332, 3, "Asia"));
            office.addPackage(new LocalPackage("Littlefinger", "The_Eyrie", 987, 7, false));
            office.printPackages(System.out);
            System.out.println();
            System.out.println("======MostExpensive======");
            System.out.println(office.mostExpensive());
        }
        if (test == 5) {
            PostOffice office = new PostOffice("Poshta", "Skopje");
            office.loadPackages(scanner);
            System.out.println("======Packages======");
            office.printPackages(System.out);
        }
        scanner.close();
    }
}
