package IspitniZadaci;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class InvalidIDException extends Exception
{
    public InvalidIDException(String id)
    {
        super(String.format("ID %s is not valid",id));
    }
}
class InvalidDimensionException extends Exception
{
    public InvalidDimensionException()
    {
        super("Dimension 0 is not allowed!");
    }
}

abstract class Shape implements Comparable<Shape>
{
    public String id;

    public Shape(String id) {
        this.id = id;
    }

    public abstract int getType();
    public abstract double area();
    public abstract double perimeter();
    public abstract String toString();
    public abstract void scale(double coef);

    public static Shape createShape(String line) throws InvalidIDException, InvalidDimensionException {
        String[] parts = line.split(" ");
        int type = Integer.parseInt(parts[0]);
        String id = parts[1];
        if (!id.matches("^[A-Za-z0-9]+") || id.length()>6) {
            throw new InvalidIDException(id);
        }
        double thirdValue = Double.parseDouble(parts[2]);
        if (thirdValue == 0.0)
        {
            throw new InvalidDimensionException();
        }
        if (parts.length == 3) {
            if (type == 1) {
                return new Circle(id, thirdValue);
            } else {
                return new Square(id, thirdValue);
            }
        } else {
            double height = Double.parseDouble(parts[3]);
            if (height == 0.0)
            {
                throw new InvalidDimensionException();
            }
            return new Rectangle(id, thirdValue, height);
        }
    }
        /*double thirdValue = Double.parseDouble(parts[2]);
        if (thirdValue == 0.0)
        {
            throw new InvalidDimensionException();
        }
        if (parts.length==4)
        {
            double height = Double.parseDouble(parts[3]);
            if (height == 0.0)
            {
                throw new InvalidDimensionException();
            }
            return new Rectangle(id,thirdValue,height);
        }
        if (type==1)
        {
            return new Circle(id,thirdValue);
        }
        else
            return new Square(id,thirdValue);*/


    @Override
    public int compareTo(Shape o) {
        return Comparator.comparing(Shape::area).compare(this,o);
    }
}
class Circle extends Shape
{
    double radius;

    public Circle(String id,double radius) {
        super(id);
        this.radius = radius;
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public double area() {
        return Math.pow(radius,2)*Math.PI;
    }

    @Override
    public double perimeter() {
        return 2*radius*Math.PI;
    }

    @Override
    public String toString() {
        return String.format("Circle -> Radius: %.2f Area: %.2f Perimeter: %.2f",
                radius,area(),perimeter());
    }

    @Override
    public void scale(double coef) {
        radius*=coef;
    }
}
class Square extends Shape
{
    double side;

    public Square(String id,double side) {
        super(id);
        this.side = side;
    }

    @Override
    public int getType() {
        return 2;
    }

    @Override
    public double area() {
        return Math.pow(side,2);
    }

    @Override
    public double perimeter() {
        return 4*side;
    }

    @Override
    public String toString() {
        return String.format("Square: -> Side: %.2f Area: %.2f Perimeter: %.2f",
                side,area(),perimeter());
    }

    @Override
    public void scale(double coef) {
        side*=coef;
    }
}
class Rectangle extends Shape
{
    double width;
    double height;

    public Rectangle(String id,double width,double height) {
        super(id);
        this.width = width;
        this.height = height;
    }

    @Override
    public int getType() {
        return 3;
    }

    @Override
    public double area() {
        return width*height;
    }

    @Override
    public double perimeter() {
        return 2*width + 2*height;
    }

    @Override
    public String toString() {
        return String.format("Rectangle: -> Sides: %.2f, %.2f Area: %.2f Perimeter: %.2f",
                width,height,area(),perimeter());
    }

    @Override
    public void scale(double coef) {
        width*=coef;
        height*=coef;
    }
}
class Canvas
{
    Set<Shape> shapes;

    public Canvas()
    {
        this.shapes = new TreeSet<>();
    }

    public void readShapes(InputStream in) throws InvalidIDException, InvalidDimensionException {
        /*BufferedReader br = new BufferedReader(new InputStreamReader(in));
        shapes = br.lines().map(line-> {
            try {
                return Shape.createShape(line);
            } catch (InvalidIDException | InvalidDimensionException e) {
                e.getMessage();
            }
            return null;
        }).collect(Collectors.toCollection(TreeSet::new));*/
        Scanner sc = new Scanner(in);
        while (sc.hasNextLine())
        {
            try {
                String line = sc.nextLine();
                shapes.add(Shape.createShape(line));
            }
            catch (InvalidIDException e)
            {
                System.out.println(e.getMessage());
            }
            catch (InvalidDimensionException e)
            {
                System.out.println(e.getMessage());
                return;
            }
        }
    }

    public void scaleShapes(String userId, double coef) {
        for (Shape shape: shapes)
        {
            if (shape.id.equals(userId))
            {
                shape.scale(coef);
            }
        }
    }

    public void printAllShapes(OutputStream out) {
        shapes.forEach(System.out::println);
    }

    public double areaSum()
    {
        return shapes.stream().mapToDouble(Shape::area).sum();
    }

    public static Comparator<Canvas> comparator = Comparator.comparing(Canvas::areaSum);

    public void printByUserId(OutputStream out) {
        /*Map<String,Set<Shape>> map =
                shapes.stream()
                        .sorted(Comparator.comparing(Shape::area).reversed())
                        .collect(Collectors.groupingBy(
                                shape -> shape.id,
                                TreeMap::new,
                                Collectors.toSet()
                        ));

         */
        /*(Map<String,Integer> shapeNumber = new TreeMap<>();
        map.keySet().forEach(
                user->
                {
                    shapeNumber.put(user,map.get(user).size());
                });
        String result = map.keySet().stream()
                .map(user -> {
                    String shape = map.get(user)
                            .stream().sorted(Comparator.comparing(Shape::perimeter)).map(Shape::toString)
                            .collect(Collectors.joining("\n"));
                    return "Shapes of user: " + user + "\n" + shape;
                }).collect(Collectors.joining("\n"));
        System.out.println(result);*/
        /*Map<String,Integer> shapeNumber = new HashMap<>();
        map.keySet().forEach(
                user->
                {
                    shapeNumber.put(user,map.get(user).size());
                });
        LinkedHashMap<String,Integer> sortedMap = new LinkedHashMap<>();
        shapeNumber.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(k->sortedMap.put(k.getKey(),k.getValue()));

        String result = sortedMap.keySet().stream()
                .map(user ->  {
                    String shape = map.get(user)
                            .stream().sorted(Comparator.comparing(Shape::perimeter))
                            .map(Shape::toString)
                            .collect(Collectors.joining("\n"));
                    return "Shapes of user: " + user + "\n" + shape;
                }).collect(Collectors.joining("\n"));
        System.out.println(result);*/
    }

    public void statistics(PrintStream out) {
        DoubleSummaryStatistics dss = shapes.stream().mapToDouble(Shape::area)
                .summaryStatistics();
        System.out.printf("count: %d\nsum: %.2f\nmin: %.2f\naverage: %.2f\nmax: %.2f\n"
                ,dss.getCount(),
                dss.getSum(),
                dss.getMin(),
                dss.getAverage(),
                dss.getMax());
    }
}

public class CanvasTest {

    public static void main(String[] args) throws InvalidIDException, InvalidDimensionException {
        Canvas canvas = new Canvas();

        System.out.println("READ SHAPES AND EXCEPTIONS TESTING");
        canvas.readShapes(System.in);

        System.out.println("BEFORE SCALING");
        canvas.printAllShapes(System.out);
        canvas.scaleShapes("123456", 1.5);
        System.out.println("AFTER SCALING");
        canvas.printAllShapes(System.out);

        System.out.println("PRINT BY USER ID TESTING");
        canvas.printByUserId(System.out);

        System.out.println("PRINT STATISTICS");
        canvas.statistics(System.out);
    }
}
