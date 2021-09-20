package VtorKolokvium;
import java.util.*;
import java.util.stream.Collectors;

class Name
{
    String name;
    int count;

    public Name(String name, int count) {
        this.name = name;
        this.count = count;
    }
    public int uniqueLetters()
    {
        Set<Character> characterSet = new HashSet<>();
        for (Character c: name.toCharArray())
        {
            characterSet.add(Character.toLowerCase(c));
        }
        return characterSet.size();
    }

    @Override
    public String toString() {
        return String.format("%s (%d) %d",name,count,uniqueLetters());
    }
}
class Names
{
    Map<String,Name> nameMap;

    public Names()
    {
        this.nameMap = new TreeMap<>();
    }

    public void addName(String name) {
        nameMap.putIfAbsent(name,new Name(name,0));
        nameMap.get(name).count++;
    }

    public void printN(int n) {
        nameMap.values()
                .stream()
                .filter(name -> name.count>=n)
                .forEach(System.out::println);
    }

    public String findName(int len, int x) {
        List<String> names = nameMap.keySet()
                .stream().filter(n->n.length()<len).collect(Collectors.toList());
        return names.get(x%names.size());
    }
}

public class NamesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        Names names = new Names();
        for (int i = 0; i < n; ++i) {
            String name = scanner.nextLine();
            names.addName(name);
        }
        n = scanner.nextInt();
        System.out.printf("===== PRINT NAMES APPEARING AT LEAST %d TIMES =====\n", n);
        names.printN(n);
        System.out.println("===== FIND NAME =====");
        int len = scanner.nextInt();
        int index = scanner.nextInt();
        System.out.println(names.findName(len, index));
        scanner.close();

    }
}

// vashiot kod ovde
