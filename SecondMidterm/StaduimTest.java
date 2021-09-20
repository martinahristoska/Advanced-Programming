package VtorKolokvium;
import java.util.*;
import java.util.stream.IntStream;

class Sector
{
    String code;
    int capacity;
    Map<Integer,Integer> seats; // key->seatNumber, value->type(0,1,2)
    int restrictions;

    public Sector(String code, int capacity) {
        this.code = code;
        this.capacity = capacity;
        this.seats = new HashMap<>();
        this.restrictions = 0;
    }

    public String getCode() {
        return code;
    }

    public int freeSeats()
    {
        return capacity - seats.size();
    }

    public void byeTicketSector(int seat,int type) throws SeatTakenException, SeatNotAllowedException {
       if (seats.containsKey(seat))
           throw new SeatTakenException();

       if ((type==1 && restrictions == 2) || (type==2 && restrictions ==1))
          throw new SeatNotAllowedException();

       if (type!=0 && restrictions == 0)
       {
           restrictions = type;
       }
       this.seats.put(seat,type);
    }

    public double calculatePercentage()
    {
        return ((double)seats.size()/capacity) * 100.0;
    }

    @Override
    public String toString() {
        return String.format("%s\t%d/%d\t%.1f%%",code,freeSeats(),capacity,calculatePercentage());
    }
}
class Stadium
{
    String name;
    Map<String,Sector> sectorMap; //key->sectorName value->Sector

    public Stadium(String name)
    {
        this.name = name;
        this.sectorMap = new HashMap<>();
    }

    public void createSectors(String[] sectorNames, int[] sectorSizes) {
        IntStream.range(0,sectorNames.length)
                .forEach(i->sectorMap.put(sectorNames[i],new Sector(sectorNames[i],sectorSizes[i])));
    }

    public void buyTicket(String sectorName, int seat, int type) throws SeatTakenException, SeatNotAllowedException {
        sectorMap.get(sectorName).byeTicketSector(seat,type);
    }

    public void showSectors() {
        sectorMap.values().stream()
                .sorted(Comparator.comparing(Sector::freeSeats).reversed()
                .thenComparing(Sector::getCode))
                .forEach(System.out::println);
    }
}

class SeatTakenException extends Exception
{
    public SeatTakenException()
    {
        super("SeatTakenException");
    }
}
class SeatNotAllowedException extends Exception
{
    public SeatNotAllowedException()
    {
        super("SeatNotAllowedException");
    }
}
public class StaduimTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] sectorNames = new String[n];
        int[] sectorSizes = new int[n];
        String name = scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            sectorNames[i] = parts[0];
            sectorSizes[i] = Integer.parseInt(parts[1]);
        }
        Stadium stadium = new Stadium(name);
        stadium.createSectors(sectorNames, sectorSizes);
        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            try {
                stadium.buyTicket(parts[0], Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]));
            } catch (SeatNotAllowedException e) {
                System.out.println("SeatNotAllowedException");
            } catch (SeatTakenException e) {
                System.out.println("SeatTakenException");
            }
        }
        stadium.showSectors();
    }
}
