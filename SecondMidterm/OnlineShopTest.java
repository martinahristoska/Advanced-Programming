package VtorKolokvium;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

enum COMPARATOR_TYPE {
    NEWEST_FIRST,
    OLDEST_FIRST,
    LOWEST_PRICE_FIRST,
    HIGHEST_PRICE_FIRST,
    MOST_SOLD_FIRST,
    LEAST_SOLD_FIRST
}

class ProductNotFoundException extends Exception {
    ProductNotFoundException(String message) {
        super(message);
    }
}

class Product {
    String category;
    String id;
    String name;
    LocalDateTime createdAt;
    double price;
    int quantitySold;

    public Product(String category, String id, String name, LocalDateTime createdAt, double price) {
        this.category = category;
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.price = price;
    }


    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public double buyProductWithQuantity(int quantity)
    {
        this.quantitySold += quantity;
        return price*quantity;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", price=" + price +
                ", quantitySold=" + quantitySold +
                '}';
    }
}

class ProductComparator
{
    public static Comparator<Product> productComparator(COMPARATOR_TYPE comparator_type)
    {
        switch (comparator_type)
        {
            case NEWEST_FIRST: return Comparator.comparing(Product::getCreatedAt).reversed();
            case OLDEST_FIRST: return Comparator.comparing(Product::getCreatedAt);
            case LOWEST_PRICE_FIRST: return Comparator.comparing(Product::getPrice);
            case HIGHEST_PRICE_FIRST: return Comparator.comparing(Product::getPrice).reversed();
            case MOST_SOLD_FIRST: return Comparator.comparing(Product::getQuantitySold).reversed();
            case LEAST_SOLD_FIRST: return Comparator.comparing(Product::getQuantitySold);
            default: return null;

        }
    }
}


class OnlineShop {

    Map<String,Product> mapById;
    Map<String,List<Product>> mapByCategory;

    OnlineShop() {
        this.mapById = new HashMap<>();
        this.mapByCategory = new HashMap<>();
    }

    void addProduct(String category, String id, String name, LocalDateTime createdAt, double price){
          /*mapById.put(id,new Product(category, id, name, createdAt, price));

          mapByCategory.putIfAbsent(category,new ArrayList<>());
          mapByCategory.get(category).add(new Product(category, id, name, createdAt, price));*/
        Product product = new Product(category, id, name, createdAt, price);
        mapById.put(id,product);
        mapByCategory.putIfAbsent(category,new ArrayList<>());
        mapByCategory.computeIfPresent(category,(k,v) ->
                {
                    v.add(product);
                    return v;
                }
        );
    }

    double buyProduct(String id, int quantity) throws ProductNotFoundException{
        if (!mapById.containsKey(id))
        {
            throw new ProductNotFoundException(String.format("Product with id %s does not exist in the online shop!",id));

        }
        return mapById.get(id).buyProductWithQuantity(quantity);
    }

    List<List<Product>> listProducts(String category, COMPARATOR_TYPE comparatorType, int pageSize) {
        List<Product> productList;
        if (category==null)
        {
            productList = new ArrayList<>(mapById.values());
        }
        else {
            productList = mapByCategory.get(category);
        }
        Comparator<Product> comparator = ProductComparator.productComparator(comparatorType);
        productList.sort(comparator);

        List<List<Product>> result = new ArrayList<>();
        if (pageSize>productList.size())
        {
            result.add(productList);
        }
        else {
            int ratio = (int) Math.ceil(productList.size()*1.0/pageSize);
            List<Integer> integerList = IntStream.range(0,ratio)
                    .map(i->i*pageSize).boxed().collect(Collectors.toList());
            integerList.forEach(i->result.add(productList.subList(i,Math.min(i+pageSize,productList.size()))));
        }
        return result;
    }

}

public class OnlineShopTest {

    public static void main(String[] args) {
        OnlineShop onlineShop = new OnlineShop();
        double totalAmount = 0.0;
        Scanner sc = new Scanner(System.in);
        String line;
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            String[] parts = line.split("\\s+");
            if (parts[0].equalsIgnoreCase("addproduct")) {
                String category = parts[1];
                String id = parts[2];
                String name = parts[3];
                LocalDateTime createdAt = LocalDateTime.parse(parts[4]);
                double price = Double.parseDouble(parts[5]);
                onlineShop.addProduct(category, id, name, createdAt, price);
            } else if (parts[0].equalsIgnoreCase("buyproduct")) {
                String id = parts[1];
                int quantity = Integer.parseInt(parts[2]);
                try {
                    totalAmount += onlineShop.buyProduct(id, quantity);
                } catch (ProductNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                String category = parts[1];
                if (category.equalsIgnoreCase("null"))
                    category=null;
                String comparatorString = parts[2];
                int pageSize = Integer.parseInt(parts[3]);
                COMPARATOR_TYPE comparatorType = COMPARATOR_TYPE.valueOf(comparatorString);
                printPages(onlineShop.listProducts(category, comparatorType, pageSize));
            }
        }
        System.out.println("Total revenue of the online shop is: " + totalAmount);

    }

    private static void printPages(List<List<Product>> listProducts) {
        for (int i = 0; i < listProducts.size(); i++) {
            System.out.println("PAGE " + (i + 1));
            listProducts.get(i).forEach(System.out::println);
        }
    }
}

