package PrvKolokvium;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

enum Color {
    RED, GREEN, BLUE
}

interface Scalable
{
    void scale(float scaleFactor);
}

interface Stackable
{
    float weight(); //ploshtina na forma
}

abstract class Shape implements Scalable,Stackable,Comparable<Shape>
{
    private String id;
    private Color color;

    public Shape(String id, Color color) {
        this.id = id;
        this.color = color;
    }
    public abstract String getType();

    @Override
    public String toString() {
        return String.format("%s: %-5s%10s%10.2f\n",getType(),id,color,weight());
    }

    @Override
    public int compareTo(Shape o) {
        return -Float.compare(this.weight(),o.weight());
    }

    public String getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }
}
class Circle extends Shape
{
    private float radius;

    public Circle(String id, Color color,float radius) {
        super(id, color);
        this.radius = radius;
    }

    @Override
    public void scale(float scaleFactor) {
        radius*=scaleFactor;
    }

    @Override
    public float weight() {
        return (float) (Math.pow(radius,2) * Math.PI);
    }

    @Override
    public String getType() {
        return "C";
    }
}
class Rectangle extends Shape
{
    private float width;
    private float height;

    public Rectangle(String id, Color color,float width,float height) {
        super(id, color);
        this.width = width;
        this.height = height;
    }

    @Override
    public void scale(float scaleFactor) {
        width*=scaleFactor;
        height*=scaleFactor;
    }

    @Override
    public float weight() {
        return width*height;
    }

    @Override
    public String getType() {
        return "R";
    }
}
class Canvas
{
    private List<Shape> shapes;

    public Canvas()
    {
        this.shapes = new ArrayList<>();
    }

    public void add(String id, Color color, float radius) {
        shapes.add(new Circle(id,color,radius));
        Collections.sort(shapes);

    }

    public void add(String id, Color color, float width,float height) {
        shapes.add(new Rectangle(id, color, width, height));
        Collections.sort(shapes);

    }

    public void scale(String id, float scaleFactor) {
        Collections.sort(shapes);
        for (Shape shape: shapes)
        {
            if (shape.getId().equals(id))
            {
                shape.scale(scaleFactor);
            }
        }
        Collections.sort(shapes);

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Shape shape: shapes)
        {
            sb.append(shape.toString());
        }
        return sb.toString();
    }
}

public class ShapesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Canvas canvas = new Canvas();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            int type = Integer.parseInt(parts[0]);
            String id = parts[1];
            if (type == 1) {
                Color color = Color.valueOf(parts[2]);
                float radius = Float.parseFloat(parts[3]);
                canvas.add(id, color, radius);
            } else if (type == 2) {
                Color color = Color.valueOf(parts[2]);
                float width = Float.parseFloat(parts[3]);
                float height = Float.parseFloat(parts[4]);
                canvas.add(id, color, width, height);
            } else if (type == 3) {
                float scaleFactor = Float.parseFloat(parts[2]);
                System.out.println("ORIGNAL:");
                System.out.print(canvas);
                canvas.scale(id, scaleFactor);
                System.out.printf("AFTER SCALING: %s %.2f\n", id, scaleFactor);
                System.out.print(canvas);
            }

        }
    }
}


