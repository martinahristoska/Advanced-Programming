package IspitniZadaci;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

interface Tuple2<T extends Comparable<T>>
{
    T getFirst();
    T getSecond();
}

class TuplesCollection<T extends Comparable<T>>
{
    Set<Tuple2<T>> tuples;

    public TuplesCollection()
    {
        tuples = new TreeSet<>((t1,t2) -> { 
            Comparator<Tuple2<T>> comparator = Comparator.comparing(Tuple2::getFirst);
            return comparator.thenComparing(Tuple2::getSecond).compare(t1,t2);
        });
    }

    public void addTuple(Tuple2<T> tuple2)
    {
        tuples.add(tuple2);
    }

    public TreeSet<Tuple2<T>> getFilteredTuples(Predicate<Tuple2<T>> predicate) {
        return tuples.stream()
                .filter(predicate)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public Map<T, List<Tuple2<T>>> groupBy(boolean first) {
        return tuples.stream()
                .collect(Collectors.toMap(
                        (k) -> first? k.getFirst() : k.getSecond(),
                        (v) -> {
                            List<Tuple2<T>> list = new ArrayList<>();
                            list.add(v);
                            return list;
                        },
                        (oldV, newV) -> {
                            oldV.add(newV.get(0));
                            return oldV;
                        },
                        TreeMap::new
                ));
    }

    public Map<T,Integer> reduceBy(boolean first) {
        return groupBy(first).entrySet()
                .stream()
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        HashMap::new,
                        Collectors.summingInt((Map.Entry<T, List<Tuple2<T>>> entry) -> entry.getValue().size())
                ));
    }

    public Collection<Tuple2<T>> uniqueTuples(boolean first) {
        return tuples.stream()
                .collect(Collectors.toCollection(() -> new TreeSet<>(
                        (t1,t2) -> {
                            if (first)
                                return t1.getFirst().compareTo(t2.getFirst());
                            return t2.getSecond().compareTo(t2.getSecond());
                        }
                )));
    }

    public int compare(TuplesCollection<? extends Comparable<?>>  otherCollection) {
        return Integer.compare(this.tuples.size(),otherCollection.tuples.size());
    }
}
class TuplesBuilder
{

    public static Tuple2<Integer> buildIntegersTuple(int int1, int int2) {
        return new Tuple2<Integer>() {
            @Override
            public Integer getFirst() {
                return int1;
            }

            @Override
            public Integer getSecond() {
                return int2;
            }
        };
    }

    public static Tuple2<Double> buildDoublesTuple(double dbl1, double dbl2) {
        return new Tuple2<Double>() {
            @Override
            public Double getFirst() {
                return dbl1;
            }

            @Override
            public Double getSecond() {
                return dbl2;
            }
        };
    }

    public static Tuple2<String> buildStringsTuple(String str1, String str2) {
        return new Tuple2<String>() {
            @Override
            public String getFirst() {
                return str1;
            }

            @Override
            public String getSecond() {
                return str2;
            }
        };
    }
}

public class TuplesCollectionTest {

    private static <T extends Comparable<T>> void printTuple(Tuple2<T> tuple) {
        System.out.println(String.format("(%s,%s)", tuple.getFirst().toString(), tuple.getSecond().toString()));

    }

    private static <T extends Comparable<T>> String tupleToString(Tuple2<T> tuple) {
        return String.format("(%s,%s)", tuple.getFirst().toString(), tuple.getSecond().toString());
    }

    private static <T extends Comparable<T>> void printCountingMaps(Map<T, Integer> countingMap) {
        countingMap.entrySet()
                .stream()
                .map(entry -> String.format("%s : %d", entry.getKey().toString(), entry.getValue()))
                .forEach(System.out::println);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TuplesCollection<Integer> integerTuplesCollection = new TuplesCollection<>();
        TuplesCollection<String> stringTuplesCollection = new TuplesCollection<>();
        TuplesCollection<Double> doubleTuplesCollection = new TuplesCollection<>();
        int n = sc.nextInt();
        for (int i = 0; i < n; i++) {
            int type = sc.nextInt();

            if (type == 1) {
                int e1, e2;
                e1 = sc.nextInt();
                e2 = sc.nextInt();
                integerTuplesCollection.addTuple(TuplesBuilder.buildIntegersTuple(e1, e2));
            } else if (type == 2) {
                double e1, e2;
                e1 = sc.nextDouble();
                e2 = sc.nextDouble();
                doubleTuplesCollection.addTuple(TuplesBuilder.buildDoublesTuple(e1, e2));
            } else {
                String e1, e2;
                e1 = sc.next();
                e2 = sc.next();
                stringTuplesCollection.addTuple(TuplesBuilder.buildStringsTuple(e1, e2));
            }
        }
        int testCase = sc.nextInt();

        Predicate<Tuple2<Integer>> lessThanFive = tuple -> tuple.getFirst() < 5 && tuple.getSecond() < 5;
        Predicate<Tuple2<Integer>> greaterThanFive = tuple -> tuple.getFirst() > 5 || tuple.getSecond() > 5;

        Predicate<Tuple2<Double>> lestThan2 = doubleTuple2 -> doubleTuple2.getFirst() < 2.0 && doubleTuple2.getSecond() < 2.0;
        Predicate<Tuple2<Double>> greaterThan2 = doubleTuple2 -> doubleTuple2.getFirst() >= 2.0 || doubleTuple2.getSecond() >= 2.0;

        Predicate<Tuple2<String>> left = stringTuple2 -> stringTuple2.getFirst().toLowerCase().compareTo("m") <= 0 && stringTuple2.getSecond().toLowerCase().compareTo("m") <= 0;
        Predicate<Tuple2<String>> right = stringTuple2 -> stringTuple2.getFirst().toLowerCase().compareTo("m") > 0 && stringTuple2.getSecond().toLowerCase().compareTo("m") > 0;


        if (testCase == 1) {
            System.out.println("===getFilteredTuples, integers, less than 5===");
            integerTuplesCollection.getFilteredTuples(lessThanFive).forEach(TuplesCollectionTest::printTuple);
            System.out.println("===getFilteredTuples, integers, greater than 5===");
            integerTuplesCollection.getFilteredTuples(greaterThanFive).forEach(TuplesCollectionTest::printTuple);
        } else if (testCase == 2) {
            System.out.println("===getFilteredTuples, doubles, less than 2.0===");
            doubleTuplesCollection.getFilteredTuples(lestThan2).forEach(TuplesCollectionTest::printTuple);
            System.out.println("===getFilteredTuples, doubles, greater than 2.0===");
            doubleTuplesCollection.getFilteredTuples(greaterThan2).forEach(TuplesCollectionTest::printTuple);
        } else if (testCase == 3) {
            System.out.println("===getFilteredTuples, Strings, left===");
            stringTuplesCollection.getFilteredTuples(left).forEach(TuplesCollectionTest::printTuple);
            System.out.println("===getFilteredTuples, Strings, right===");
            stringTuplesCollection.getFilteredTuples(right).forEach(TuplesCollectionTest::printTuple);
        } else if (testCase == 4) {

            System.out.println("reduceBy(first)");
            printCountingMaps(integerTuplesCollection.reduceBy(true));
            System.out.println("groupBy(first)");
            printElementsMaps(integerTuplesCollection.groupBy(true));
            System.out.println("uniqueTuples(first)");
            integerTuplesCollection.uniqueTuples(true).forEach(TuplesCollectionTest::printTuple);

            System.out.println("reduceBy(second)");
            printCountingMaps(integerTuplesCollection.reduceBy(false));
            System.out.println("groupBy(second)");
            printElementsMaps(integerTuplesCollection.groupBy(false));
            System.out.println("uniqueTuples(second)");
            integerTuplesCollection.uniqueTuples(false).forEach(TuplesCollectionTest::printTuple);

        } else if (testCase == 5) {
            System.out.println("reduceBy(first)");
            printCountingMaps(doubleTuplesCollection.reduceBy(true));
            System.out.println("groupBy(first)");
            printElementsMaps(doubleTuplesCollection.groupBy(true));
            System.out.println("uniqueTuples(first)");
            doubleTuplesCollection.uniqueTuples(true).forEach(TuplesCollectionTest::printTuple);

            System.out.println("reduceBy(second)");
            printCountingMaps(doubleTuplesCollection.reduceBy(false));
            System.out.println("groupBy(second)");
            printElementsMaps(doubleTuplesCollection.groupBy(false));
            System.out.println("uniqueTuples(second)");
            doubleTuplesCollection.uniqueTuples(false).forEach(TuplesCollectionTest::printTuple);
        } else if (testCase == 6) {
            System.out.println("reduceBy(first)");
            printCountingMaps(stringTuplesCollection.reduceBy(true));
            System.out.println("groupBy(first)");
            printElementsMaps(stringTuplesCollection.groupBy(true));
            System.out.println("uniqueTuples(first)");
            stringTuplesCollection.uniqueTuples(true).forEach(TuplesCollectionTest::printTuple);

            System.out.println("reduceBy(second)");
            printCountingMaps(stringTuplesCollection.reduceBy(false));
            System.out.println("groupBy(second)");
            printElementsMaps(stringTuplesCollection.groupBy(false));
            System.out.println("uniqueTuples(second)");
            stringTuplesCollection.uniqueTuples(false).forEach(TuplesCollectionTest::printTuple);
        }
        else if (testCase == 7) {
            System.out.println("===compare===");
            System.out.println(stringTuplesCollection.compare(doubleTuplesCollection));
            System.out.println(stringTuplesCollection.compare(integerTuplesCollection));
            System.out.println(integerTuplesCollection.compare(doubleTuplesCollection));
            System.out.println(integerTuplesCollection.compare(integerTuplesCollection));
        }
    }

    private static <T extends Comparable<T>> void printElementsMaps(Map<T, List<Tuple2<T>>> map) {
        map.entrySet()
                .stream()
                .map(entry -> String.format("%s : [%s]",
                        entry.getKey().toString(),
                        entry.getValue().stream().map(TuplesCollectionTest::tupleToString).collect(Collectors.joining(", "))))
                .forEach(System.out::println);
    }
}
