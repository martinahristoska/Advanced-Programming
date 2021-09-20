package IspitniZadaci;
import java.util.*;

interface PointInterface
{
     long getId();
     float getX();
     float getY();
     double calculateDistance(PointInterface pointInterface);
}
class Cluster<T extends PointInterface>
{
    List<T> elements;

    public Cluster()
    {
        this.elements = new ArrayList<>();
    }

    public void addItem(T element) {
        elements.add(element);
    }

    public void near(int id, int top) {
       T element = null;
       for (T cluster: elements)
       {
           if (cluster.getId()==id)
           {
               element = cluster;
           }
       }
       T finalElement = element;
       elements.sort(Comparator.comparing(el->el.calculateDistance(finalElement)));
       for (int i=0;i<top;i++)
       {
           System.out.println(String.format("%d. %d -> %.3f",i+1,
                   elements.get(i+1).getId(),elements.get(i+1).calculateDistance(finalElement)));
       }
    }
}
class Point2D implements PointInterface
{
    long id;
    float x;
    float y;

    public Point2D(long id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public double calculateDistance(PointInterface pointInterface) {
        return Math.sqrt(Math.pow((this.x - pointInterface.getX()),2) +
                Math.pow((this.y - pointInterface.getY()),2));
    }


}

public class ClusterTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Cluster<Point2D> cluster = new Cluster<>();
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            long id = Long.parseLong(parts[0]);
            float x = Float.parseFloat(parts[1]);
            float y = Float.parseFloat(parts[2]);
            cluster.addItem(new Point2D(id, x, y));
        }
        int id = scanner.nextInt();
        int top = scanner.nextInt();
        cluster.near(id, top);
        scanner.close();
    }
}

