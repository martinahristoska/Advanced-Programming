package Lab3;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

interface Item
{
     int getPrice();
}

class InvalidPizzaTypeException extends Exception
{
    public InvalidPizzaTypeException()
    {
        super("Invalid pizza type");
    }
}
class InvalidExtraTypeException extends Exception
{
    public InvalidExtraTypeException()
    {
        super("Invalid extra type");
    }
}
class ItemOutOfStockException extends Exception
{
    public ItemOutOfStockException(Item item)
    {
        super(String.format("%s is out of stock",item));
    }
}
class OrderLockedException extends Exception{
    public OrderLockedException()
    {
        super("Order is locked");
    }
}
class EmptyOrder extends Exception {
    public EmptyOrder(){
        super("EmptyOrder");
    }
}
class ArrayIndexOutOfBoundsException extends Exception{
    public ArrayIndexOutOfBoundsException(int idx) {
        super(idx + "");
    }
}

class PizzaItem implements Item
{
    private String type;

    public PizzaItem(String type) throws InvalidPizzaTypeException {
        if (type.equals("Standard") || type.equals("Pepperoni") || type.equals("Vegetarian"))
        {
            this.type = type;
        }
        else throw new InvalidPizzaTypeException();
    }

    @Override
    public int getPrice() {
        if (type.equals("Standard"))
            return 10;
        else if (type.equals("Pepperoni"))
            return 12;
        else return 8;
    }

    @Override
    public String toString() {
        return type;
    }
}
class ExtraItem implements Item
{
    private String type;

    public ExtraItem(String type) throws InvalidExtraTypeException {
        if (type.equals("Ketchup") || type.equals("Coke"))
        {
            this.type = type;
        }
        else throw new InvalidExtraTypeException();
    }

    @Override
    public int getPrice() {
        if (type.equals("Ketchup")) return 3;
        else return 5;
    }

    @Override
    public String toString() {
        return type;
    }
}
class Order
{
    private List<Item> items;
    private List<Integer> kolicina;
    private boolean isLocked;

    public Order() {
        this.items = new ArrayList<>();
        this.kolicina = new ArrayList<>();
        this.isLocked = false;
    }

    public void addItem(Item item, int count) throws ItemOutOfStockException, OrderLockedException {
        if (isLocked)
            throw new OrderLockedException();
        if(count>10)
            throw new ItemOutOfStockException(item);
        int index = findIndex(item);
        if (index == -1)
        {
            items.add(item);
            kolicina.add(count);
        }
        else {
            kolicina.set(index,count);
        }
    }
    public int findIndex(Item item)
    {
        for (int i=0;i<items.size();i++)
        {
            if (items.get(i).toString().equals(item.toString()))
            {
                return i;
            }
        }
        return -1;
        /*int index = -1;
        for (Item item: items)
        {
            if (item.toString().equals(i.toString()))
            {
                index = items.indexOf(item);
            }
        }
        return index;*/
    }

    public int getPrice() {
        int sum = 0;
        for (Item item: items)
        {
            sum+=item.getPrice() * kolicina.get(items.indexOf(item));
        }
        return sum;
    }

    public void displayOrder() {
        StringBuilder sb = new StringBuilder();
        for (Item item: items)
        {
            int index = items.indexOf(item);
            sb.append(String.format("%3d.%-15sx%2d%5d$\n",
                    index+1,item.toString(),
                    kolicina.get(index),
                    item.getPrice()*kolicina.get(index)));
        }
        sb.append(String.format("%-22s%5d$","Total:",getPrice()));
        System.out.println(sb.toString());
    }

    public void removeItem(int idx) throws ArrayIndexOutOfBoundsException, OrderLockedException {
        if (isLocked) throw new OrderLockedException();
        if (idx<0 || idx>items.size()) throw new ArrayIndexOutOfBoundsException(idx);
        items.remove(idx);
    }

    public void lock() throws EmptyOrder {
        if (items.size()!=0)
            isLocked = true;
        else throw new EmptyOrder();
    }
}

public class PizzaOrderTest {

    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if (k == 0) { //test Item
            try {
                String type = jin.next();
                String name = jin.next();
                Item item = null;
                if (type.equals("Pizza")) item = new PizzaItem(name);
                else item = new ExtraItem(name);
                System.out.println(item.getPrice());
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
        }
        if (k == 1) { // test simple order
            Order order = new Order();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            jin.next();
            System.out.println(order.getPrice());
            order.displayOrder();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            System.out.println(order.getPrice());
            order.displayOrder();
        }
        if (k == 2) { // test order with removing
            Order order = new Order();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            jin.next();
            System.out.println(order.getPrice());
            order.displayOrder();
            while (jin.hasNextInt()) {
                try {
                    int idx = jin.nextInt();
                    order.removeItem(idx);
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            System.out.println(order.getPrice());
            order.displayOrder();
        }
        if (k == 3) { //test locking & exceptions
            Order order = new Order();
            try {
                order.lock();
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
            try {
                order.addItem(new ExtraItem("Coke"), 1);
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
            try {
                order.lock();
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
            try {
                order.removeItem(0);
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
        }
    }

}