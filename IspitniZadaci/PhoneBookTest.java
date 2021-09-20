package IspitniZadaci;
import java.util.*;

class Contact implements Comparable<Contact>
{
    private String name;
    private String number;

    public Contact(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public List<String> subNumbers()
    {
        List<String> result = new ArrayList<>();
        for (int i=3;i<=number.length();i++)
        {
            for (int j=0;j<=number.length()-i;j++)
            {
                result.add(number.substring(j,j+i));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s %s",name,number);
    }

    @Override
    public int compareTo(Contact o) {
        return Comparator.comparing(Contact::getName)
                .thenComparing(Contact::getNumber).compare(this,o);
    }
}

class PhoneBook
{
    Map<String,Set<Contact>> contactsByName;
    Map<String,Set<Contact>> contactsBySubNumbers;
    Map<String,Contact> contactByNumber;

    public PhoneBook()
    {
        this.contactsByName = new HashMap<>();
        this.contactsBySubNumbers = new HashMap<>();
        this.contactByNumber = new HashMap<>();
    }

    public void addContact(String name, String number) throws DuplicateNumberException {
        if (contactByNumber.containsKey(number))
        {
            throw new DuplicateNumberException(number);
        }
        contactByNumber.put(number,new Contact(name, number));
        Contact contact = new Contact(name, number);
        contactsByName.putIfAbsent(name,new TreeSet<>());
        contactsByName.get(name).add(contact);
        for (String subNum: contact.subNumbers())
        {
            contactsBySubNumbers.putIfAbsent(subNum,new TreeSet<>());
            contactsBySubNumbers.get(subNum).add(contact);
        }
    }

    public void contactsByNumber(String number) {
        if (!contactsBySubNumbers.containsKey(number))
        {
            System.out.println("NOT FOUND");
        }
        else {
            contactsBySubNumbers.get(number).forEach(System.out::println);
        }
    }

    public void contactsByName(String name) {
        if(!contactsByName.containsKey(name))
        {
            System.out.println("NOT FOUND");
        }
        else {
            contactsByName.get(name).forEach(System.out::println);
        }
    }
}
class DuplicateNumberException extends Exception
{
    public DuplicateNumberException(String number)
    {
        super(String.format("Duplicate number: %s",number));
    }
}

public class PhoneBookTest {
    public static void main(String[] args) {
//        Contact c = new Contact("stefan", "123456789");
//        System.out.println(c.getSubNumbers());
        PhoneBook phoneBook = new PhoneBook();
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");

            try {
                phoneBook.addContact(parts[0], parts[1]);
            } catch (DuplicateNumberException e) {
                System.out.println(e.getMessage());
            }

        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println(line);
            String[] parts = line.split(":");
            if (parts[0].equals("NUM")) {
                phoneBook.contactsByNumber(parts[1]);
            } else {
                phoneBook.contactsByName(parts[1]);
            }
        }
    }
}

