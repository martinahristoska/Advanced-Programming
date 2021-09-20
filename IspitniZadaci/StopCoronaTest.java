package IspitniZadaci;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class UserAlreadyExistException extends Exception
{
    public UserAlreadyExistException(String id)
    {
        super(String.format("User with id %s already exists",id));
    }
}

interface ILocation{
    double getLongitude();

    double getLatitude();

    LocalDateTime getTimestamp();
}
class User
{
    private String name;
    private String id;
    private List<ILocation> locations;

    public User(String name, String id) {
        this.name = name;
        this.id = id;
        this.locations = new ArrayList<>();
    }

    public void addLocation(List<ILocation> iLocations)
    {
        locations.addAll(iLocations);
    }

    public int countCloseContacts(User user)
    {
        return locations.stream()
                .flatMapToInt(i -> user.locations.stream()
                .mapToInt(j -> LocationDistance.isDanger(i,j) ? 1:0))
                .sum();
    }

    public String complete()
    {
        return String.format("%s %s",name,id);
    }
    public String hidden()
    {
        return String.format("%s %s***",name,id.substring(0,4));
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<ILocation> getLocations() {
        return locations;
    }

}
class LocationDistance
{
    public static double distanceBetween(ILocation location1,ILocation location2)
    {
        return Math.sqrt(Math.pow(location1.getLongitude() - location2.getLongitude(),2)
                + Math.pow(location1.getLatitude() - location2.getLatitude(),2));
    }

    public static double timeBetween(ILocation location1,ILocation location2)
    {
        return Math.abs(Duration.between(location1.getTimestamp(),location2.getTimestamp()).getSeconds());
    }
    public static boolean isDanger(ILocation location1,ILocation location2)
    {
        return distanceBetween(location1, location2)<=2.0 && timeBetween(location1, location2)<=300;
    }
}
class StopCoronaApp
{
    Map<String,User> userById;
    Map<String,LocalDateTime> infectedMap;

    public StopCoronaApp()
    {
        this.userById = new HashMap<>();
        this.infectedMap = new HashMap<>();
    }

    public void addUser(String name, String id) throws UserAlreadyExistException {
        if (userById.containsKey(id))
        {
            throw new UserAlreadyExistException(id);
        }
        userById.put(id,new User(name, id));
    }

    public void addLocations(String id, List<ILocation> locations) {
        userById.get(id).addLocation(locations);
    }

    public void detectNewCase(String id, LocalDateTime timestamp) {
        infectedMap.put(id,timestamp);
    }
    public Map<User,Integer> getDirectContacts(User u)
    {
         Map<User,Integer> result = new HashMap<>();
         userById.values().stream()
                 .filter(user -> !user.equals(u))
                 .filter(user -> user.countCloseContacts(u)!=0)
                 .forEach(user -> result.put(user,u.countCloseContacts(user)));
        return result;
    }
    public Collection<User> getIndirectContacts(User u)
    {
       Set<User> indirectContacts = new TreeSet<>(Comparator.comparing(User::getName).thenComparing(User::getId));
       Map<User,Integer> directContacts = getDirectContacts(u);

       directContacts.keySet().stream()
               .flatMap(user -> getDirectContacts(user).keySet().stream())
               .filter(user -> !indirectContacts.contains(user) && !directContacts.containsKey(user) && !user.equals(u))
               .forEach(indirectContacts::add);
       return indirectContacts;
    }

    public void createReport() {
        List<Integer> countOfDirectContacts = new ArrayList<>();
        List<Integer> countOfIndirectContacts = new ArrayList<>();

        infectedMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry-> printInfectedUserEntry(entry,countOfDirectContacts,countOfIndirectContacts));

        System.out.printf("Average direct contacts: %.4f\n",countOfDirectContacts.stream().mapToInt(i->i).average().getAsDouble());
        System.out.printf("Average indirect contacts: %.4f\n", countOfIndirectContacts.stream().mapToInt(i -> i).average().getAsDouble());

    }

    public void printInfectedUserEntry(Map.Entry<String,LocalDateTime> entry,
                                       List<Integer> countOfDirectContacts,
                                       List<Integer> countOfIndirectContacts)
    {
        User user = userById.get(entry.getKey());
        System.out.printf("%s %s\n",user.complete(),entry.getValue());
        System.out.println("Direct contacts:");

        Map<User,Integer> directContacts = getDirectContacts(user);

        directContacts.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(e -> System.out.printf("%s %d\n",e.getKey().hidden(),e.getValue()));

        int countOfDirectContact = directContacts.values()
                .stream()
                .mapToInt(d->d).sum();
        System.out.printf("Count of direct contacts: %d\n",countOfDirectContact);
        countOfDirectContacts.add(countOfDirectContact);
        System.out.println("Indirect contacts: ");

        Collection<User> indirectContact = getIndirectContacts(user);
        indirectContact.forEach(u-> System.out.println(u.hidden()));
        System.out.printf("Count of indirect contacts: %d\n",indirectContact.size());
        countOfIndirectContacts.add(indirectContact.size());
    }
}

public class StopCoronaTest {

    public static double timeBetweenInSeconds(ILocation location1, ILocation location2) {
        return Math.abs(Duration.between(location1.getTimestamp(), location2.getTimestamp()).getSeconds());
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        StopCoronaApp stopCoronaApp = new StopCoronaApp();

        while (sc.hasNext()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            switch (parts[0]) {
                case "REG": //register
                    String name = parts[1];
                    String id = parts[2];
                    try {
                        stopCoronaApp.addUser(name, id);
                    } catch (UserAlreadyExistException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "LOC": //add locations
                    id = parts[1];
                    List<ILocation> locations = new ArrayList<>();
                    for (int i = 2; i < parts.length; i += 3) {
                        locations.add(createLocationObject(parts[i], parts[i + 1], parts[i + 2]));
                    }
                    stopCoronaApp.addLocations(id, locations);

                    break;
                case "DET": //detect new cases
                    id = parts[1];
                    LocalDateTime timestamp = LocalDateTime.parse(parts[2]);
                    stopCoronaApp.detectNewCase(id, timestamp);

                    break;
                case "REP": //print report
                    stopCoronaApp.createReport();
                    break;
                default:
                    break;
            }
        }
    }

    private static ILocation createLocationObject(String lon, String lat, String timestamp) {
        return new ILocation() {
            @Override
            public double getLongitude() {
                return Double.parseDouble(lon);
            }

            @Override
            public double getLatitude() {
                return Double.parseDouble(lat);
            }

            @Override
            public LocalDateTime getTimestamp() {
                return LocalDateTime.parse(timestamp);
            }
        };
    }
}
