package IspitniZadaci;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

interface Identifiable  {

    Integer getId();
}

//TODO: fill in the ??? places, if necessary
class Item <V> implements Identifiable {

    private final V item;

    public Item(V item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return item.toString();
    }

    @Override
    public  Integer getId() {
        return Math.abs(item.hashCode()) % 5;
    }
}

//TODO: fill in the ??? places, if necessary
public class GenericMap{

    public static <V extends Identifiable> Map<Integer, List<V>> listToMap(List<V> list) {

       Map<Integer,List<V>> map = list.stream()
               .collect(Collectors.groupingBy(
                       Identifiable::getId,
                       TreeMap::new,
                       Collectors.toList()
               ));
       return map;
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String line;

        while(!(line = scanner.nextLine()).equals("END")) {
            int testCase = Integer.parseInt(line);

            switch (testCase) {
                case 1: {
                    System.out.println("------------ TEST STRING ------------");
                    List<Item<String>> list1 = new ArrayList<>();

                    list1.add(new Item<>("This is a string"));
                    list1.add(new Item<>("Wonderful day"));
                    list1.add(new Item<>("The weather is nice and sunny"));
                    list1.add(new Item<>("It is an easy exam"));
                    list1.add(new Item<>("One coffee"));

                    Map<Integer, List<Item<String>>> map = listToMap(list1);
                    map.forEach((key, value) -> System.out.println(key + " " + value));
                }
                case 2: {
                    System.out.println("------------ TEST INTEGER ------------");
                    List<Item<Integer>> list2 = new ArrayList<>();

                    list2.add(new Item<>(2));
                    list2.add(new Item<>(31));
                    list2.add(new Item<>(78));
                    list2.add(new Item<>(909));
                    list2.add(new Item<>(2191));
                    list2.add(new Item<>(8918));
                    list2.add(new Item<>(1516));
                    list2.add(new Item<>(17817));
                    list2.add(new Item<>(111));
                    list2.add(new Item<>(222));
                    list2.add(new Item<>(4544));
                    list2.add(new Item<>(888));
                    list2.add(new Item<>(678));
                    list2.add(new Item<>(8898));

                    Map<Integer, List<Item<Integer>>> map = listToMap(list2);
                    map.forEach((key, value) -> System.out.println(key + " " + value));
                }
                case 3: {
                    System.out.println("------------ TEST ONE ITEM ------------");
                    List<Item<Integer>> list3 = new ArrayList<>();

                    list3.add(new Item<>(234));

                    Map<Integer, List<Item<Integer>>> map = listToMap(list3);
                    map.forEach((key, value) -> System.out.println(key + " " + value));
                }
            }
        }
    }
}
