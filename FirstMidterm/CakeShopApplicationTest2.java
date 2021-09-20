package PrvKolokvium;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

enum Type {
    CAKE,
    PIE
}

abstract class Itemm
{
    private String name;
    private int price;

    public Itemm(String name) {
        this.name = name;
        this.price = 0;
    }

    public String getName() {
        return name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }
    abstract Type getType();
}

class Cake extends Itemm
{

    public Cake(String name) {
        super(name);
    }

    @Override
    Type getType() {
        return Type.CAKE;
    }

}
class Pie extends Itemm
{

    public Pie(String name) {
        super(name);
    }

    @Override
    Type getType() {
        return Type.PIE;
    }

    @Override
    public int getPrice() {
        return super.getPrice() + 50;
    }
}
class Order implements Comparable<Order>
{
    private int id;
    private List<Itemm> items;

    public Order(int id, List<Itemm> items) {
        this.id = id;
        this.items = items;
    }

    public int getId() {
        return id;
    }

   public int totalOrderItems()
   {
       return items.size();
   }
   public int totalPies()
   {
       return (int) items.stream().filter(p->p.getType().equals("PIE")).count();
   }
   public int totalCakes()
   {
       return totalOrderItems() - totalPies();
   }
   public int totalAmount()
   {
       return items.stream().mapToInt(Itemm::getPrice).sum();
   }

    @Override
    public int compareTo(Order o) {
        return Comparator.comparing(Order::totalAmount).reversed()
                .compare(this,o);
    }
    public static Order createOrder(String line,int minOrders) throws InvalidOrderException {
        String [] parts = line.split("\\s+");
        int orderId = Integer.parseInt(parts[0]);
        List<Itemm> items = new ArrayList<>();
        for (int i=1;i<parts.length;i++)
        {
            if (Character.isAlphabetic(parts[i].charAt(0)))
            {
                if (parts[i].charAt(0) == 'C')
                {
                    items.add(new Cake(parts[i]));
                }
                else {
                    items.add(new Pie(parts[i]));
                }
            }
            else {
                items.get(items.size()-1).setPrice(Integer.parseInt(parts[i]));
            }

        }
        if (items.size()<minOrders) throw new InvalidOrderException(orderId);
        return new Order(orderId,items);
    }

    @Override
    public String toString() {
        return String.format("%d %d %d %d %d",
                id,totalOrderItems(),totalPies(),totalCakes(),totalAmount());
    }
}

class InvalidOrderException extends Exception
{
    public InvalidOrderException(int id)
    {
        super(String.format("The order with id %d has less items than the minimum allowed.",id));
    }
}

class CakeShopApplication
{

    private List<Order> orders;
    private int minOrderItems;

    public CakeShopApplication(int minOrderItems)
    {
        this.minOrderItems = minOrderItems;
        this.orders = new ArrayList<>();
    }

    public void readCakeOrders(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        orders = br.lines().map(line -> {
            try {
                return Order.createOrder(line,minOrderItems);
            } catch (InvalidOrderException e) {
                System.out.println(e.getMessage());
                return null;
            }
        }).collect(Collectors.toList());
    }

    public void printAllOrders(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        orders.stream().sorted(Order::compareTo).forEach(pw::println);
        pw.flush();
    }
}

public class CakeShopApplicationTest2 {

    public static void main(String[] args) {
        CakeShopApplication cakeShopApplication = new CakeShopApplication(4);

        System.out.println("--- READING FROM INPUT STREAM ---");
        cakeShopApplication.readCakeOrders(System.in);

        System.out.println("--- PRINTING TO OUTPUT STREAM ---");
        cakeShopApplication.printAllOrders(System.out);
    }
}