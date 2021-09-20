package VtorKolokvium;
import java.util.*;

class Component implements Comparable<Component>
{
    String color;
    private int weight;
    Set<Component> components;

    public Component(String color, int weight) {
        this.color = color;
        this.weight = weight;
        this.components = new TreeSet<>();
    }

    public void addComponent(Component component) {
        components.add(component);
    }

    public String getColor() {
        return color;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public int compareTo(Component o) {
        int res = Integer.compare(this.weight,o.weight);
        if (res==0)
        {
            return this.color.compareTo(o.color);
        }
        return res;
    }

    public void changeColorCom(int weight,String color)
    {
        if (this.weight<weight)
        {
            this.color = color;
        }
        components.forEach(component -> component.changeColorCom(weight, color));
    }

    public String pecati(String line)
    {
        String s = String.format("%s%d:%s\n",line,weight,color);
        for (Component component: components)
        {
            s+=component.pecati(line + "---");
        }
        return s;
    }

    @Override
    public String toString() {
        return pecati("");
    }
}
class Window
{
    private String name;
    Map<Integer,Component> componentMap;

    public Window(String name) {
        this.name = name;
        this.componentMap = new TreeMap<>();
    }

    public void addComponent(int position, Component component) throws InvalidPositionException {
        if(componentMap.containsKey(position))
        {
            throw new InvalidPositionException(position);
        }
        componentMap.put(position,component);
    }

    public void changeColor(int weight, String color) {
        componentMap.values()
                .forEach(component -> component.changeColorCom(weight, color));

    }

    public void swichComponents(int pos1, int pos2) {
        Component component1 = componentMap.get(pos1);
        Component component2 = componentMap.get(pos2);

        componentMap.put(pos2,component1);
        componentMap.put(pos1,component2);

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WINDOW " + name + "\n");
        componentMap.entrySet().forEach(entry->sb.append(entry.getKey() + ":" + entry.getValue()));
        return sb.toString();
    }
}
class InvalidPositionException extends Exception
{
    public InvalidPositionException(int pos)
    {
        super(String.format("Invalid position %d, alredy taken!",pos));
    }
}

public class ComponentTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        Window window = new Window(name);
        Component prev = null;
        while (true) {
            try {
                int what = scanner.nextInt();
                scanner.nextLine();
                if (what == 0) {
                    int position = scanner.nextInt();
                    window.addComponent(position, prev);
                } else if (what == 1) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev = component;
                } else if (what == 2) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev.addComponent(component);
                    prev = component;
                } else if (what == 3) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev.addComponent(component);
                } else if(what == 4) {
                    break;
                }

            } catch (InvalidPositionException e) {
                System.out.println(e.getMessage());
            }
            scanner.nextLine();
        }

        System.out.println("=== ORIGINAL WINDOW ===");
        System.out.println(window);
        int weight = scanner.nextInt();
        scanner.nextLine();
        String color = scanner.nextLine();
        window.changeColor(weight, color);
        System.out.println(String.format("=== CHANGED COLOR (%d, %s) ===", weight, color));
        System.out.println(window);
        int pos1 = scanner.nextInt();
        int pos2 = scanner.nextInt();
        System.out.println(String.format("=== SWITCHED COMPONENTS %d <-> %d ===", pos1, pos2));
        window.swichComponents(pos1, pos2);
        System.out.println(window);
    }
}

// вашиот код овде
