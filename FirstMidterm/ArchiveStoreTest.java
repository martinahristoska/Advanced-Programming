package PrvKolokvium;
import java.awt.*;
import java.util.*;
import java.util.List;

class Archive
{
     int id;
    private Date dateArchived;

    public Archive(int id) {
        this.id = id;
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        this.dateArchived = new Date();
    }

    public String archiveArchive(Date date)
    {
        return String.format("Item %d archived at %s",id,date);
    }
    public String openArchive(Date date)
    {
        return String.format("Item %d opened at %s",id,date);
    }
}
class LockedArchive extends Archive
{
    private Date dateToOpen;

    public LockedArchive(int id, Date dateToOpen) {
        super(id);
        this.dateToOpen = dateToOpen;
    }

    @Override
    public String openArchive(Date date) {
        if (date.before(dateToOpen))
        {
            return String.format("Item %d cannot be opened before %s",id,dateToOpen);
        }
        return super.openArchive(date);
    }
}
class SpecialArchive extends Archive
{
    private int maxOpen;
    private int opened;

    public SpecialArchive(int id, int maxOpen) {
        super(id);
        this.maxOpen = maxOpen;
        this.opened = maxOpen;
    }

    @Override
    public String openArchive(Date date) {
        if (opened==0)
        {
            return String.format("Item %d cannot be opened more than %d times",id,maxOpen);
        }
        opened--;
        return super.openArchive(date);
    }
}

class ArchiveStore
{
     private List<Archive> archives;
     private List<String> logs;

     public ArchiveStore()
     {
        this.archives = new ArrayList<>();
        this.logs = new ArrayList<>();
     }

     public void archiveItem(Archive item, Date date) {
         this.archives.add(item);
         this.logs.add(item.archiveArchive(date));
     }

     public void openItem(int id, Date date) throws NonExistingItemException {
         for (Archive archive: archives)
         {
             if (archive.id == id)
             {
                 this.logs.add(archive.openArchive(date));
                 return;
             }
         }
         throw new NonExistingItemException(String.format("Item with id %d doesn't exist",id));
     }

     public String getLog() {
         StringBuilder sb = new StringBuilder();
         for (String log: logs)
         {
             sb.append(log + "\n");
         }
         return sb.toString();
     }
}
class NonExistingItemException extends Exception
{
    public NonExistingItemException(String text)
    {
        super(text);
    }
}

public class ArchiveStoreTest {
    public static void main(String[] args) {
        ArchiveStore store = new ArchiveStore();
        Date date = new Date(113, 10, 7);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        int n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        int i;
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            long days = scanner.nextLong();
            Date dateToOpen = new Date(date.getTime() + (days * 24 * 60
                    * 60 * 1000));
            LockedArchive lockedArchive = new LockedArchive(id, dateToOpen);
            store.archiveItem(lockedArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            int maxOpen = scanner.nextInt();
            SpecialArchive specialArchive = new SpecialArchive(id, maxOpen);
            store.archiveItem(specialArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        while(scanner.hasNext()) {
            int open = scanner.nextInt();
            try {
                store.openItem(open, date);
            } catch(NonExistingItemException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println(store.getLog());
    }
}

// вашиот код овде



