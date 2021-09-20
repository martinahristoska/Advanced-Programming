package IspitniZadaci;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

class Product implements Comparable<Product>
{
    int regularPrice;
    int discountPrice;

    public Product(int discountPrice, int regularPrice) {
        this.discountPrice = discountPrice;
        this.regularPrice = regularPrice;

    }

    public int percentDiscount()
    {
        return (int) ((1-(discountPrice*1.0/regularPrice)) * 100);
    }

    public int absoluteDiscount()
    {
        return regularPrice - discountPrice;
    }

    @Override
    public String toString() {
        return String.format("%2d%% %d/%d\n",percentDiscount(),discountPrice,regularPrice);
    }

    @Override
    public int compareTo(Product o) {
        return Comparator.comparing(Product::percentDiscount)
                .thenComparing(Product::absoluteDiscount)
                .reversed().compare(this,o);
    }
}

class Store implements Comparable<Store>
{
    String name;
    Set<Product> products;

    public Store(String name, Set<Product> products) {
        this.name = name;
        this.products = products;
    }

    public double averageDiscount()
    {
        return products.stream().mapToDouble(Product::percentDiscount).average().getAsDouble();
    }

    public int totalDiscount()
    {
        return products.stream().mapToInt(Product::absoluteDiscount).sum();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name + "\n");
        sb.append(String.format("Average discount: %.1f%%\n",averageDiscount()));
        sb.append(String.format("Total discount: %d\n",totalDiscount()));
        products.forEach(p->sb.append(p.toString()));
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Store o) {
        return Comparator.comparing(Store::averageDiscount).reversed()
                .thenComparing(Store::getName)
                .compare(this,o);
    }
}
class Discounts
{
    Set<Store> stores;

    public Discounts()
    {
        this.stores = new TreeSet<>();
    }

    public int readStores(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = br.readLine())!=null)
        {
            Set<Product> products = new TreeSet<>();
            String [] parts = line.split("\\s+");
            String name = parts[0];
            for (int i=1;i<parts.length;i++)
            {
                String [] prices = parts[i].split(":");
                Product product = new Product(Integer.parseInt(prices[0]),Integer.parseInt(prices[1]));
                products.add(product);
            }
            Store store = new Store(name,products);
            stores.add(store);
        }
        return stores.size();
    }

    public List<Store> byAverageDiscount() {
        return stores.stream()
                .limit(3)
                .collect(Collectors.toList());
    }

    public List<Store> byTotalDiscount() {
        return stores.stream()
                .sorted(Comparator.comparing(Store::totalDiscount).thenComparing(Store::getName))
                .limit(3)
                .collect(Collectors.toList());
    }
}

public class DiscountsTest {
    public static void main(String[] args) throws IOException {
        Discounts discounts = new Discounts();
        int stores = discounts.readStores(System.in);
        System.out.println("Stores read: " + stores);
        System.out.println("=== By average discount ===");
        discounts.byAverageDiscount().forEach(System.out::println);
        System.out.println("=== By total discount ===");
        discounts.byTotalDiscount().forEach(System.out::println);

    	/*Product p = new Product(2579,4985);
    	System.out.println(p.toString());
    	String input = "Levis 6385:9497  9988:19165  7121:11287  1501:2316  2579:4985  6853:8314";
    	Store store = new Store(input);
    	System.out.println(store.toString());*/
    }
}

