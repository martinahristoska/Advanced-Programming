package PrvKolokvium;
import java.io.*;
import java.util.*;

class IrregularCanvasException extends Exception
{
    String id;
    double maxArea;
    public IrregularCanvasException(String id, double maxArea)
    {
        this.id = id;
        this.maxArea = maxArea;
    }

    @Override
    public String getMessage() {
        return String.format("Canvas %s has a shape with area larger than %.2f",
                id,maxArea);
    }
}

abstract class Shape2
{
    int size;

    public Shape2(int size) {
        this.size = size;
    }
    abstract char getType();
    abstract double area();

    public static Shape2 createShape(int size,char type,double maxArea)
    {
        switch (type)
        {
            case 'C' : return new Circle2(size);
            case 'S' : return new Square(size);
            default: return null;
        }
    }
}
class Circle2 extends Shape2
{

    public Circle2(int size) {
        super(size);
    }

    @Override
    char getType() {
        return 'C';
    }

    @Override
    double area() {
        return Math.pow(size,2)*Math.PI;
    }
}
class Square extends Shape2
{

    public Square(int size) {
        super(size);
    }

    @Override
    char getType() {
        return 'S';
    }

    @Override
    double area() {
        return size*size;
    }
}

class Canvas2 implements Comparable<Canvas2>
{
    String id;
    List<Shape2> shapes;

    public Canvas2(String id, List<Shape2> shapes) {
        this.id = id;
        this.shapes = shapes;
    }

    public int totalCircles()
    {
        return (int) shapes.stream().filter(s->s.getType() == 'C').count();
    }
    //b0ee5b7c S 24 C 17 C 24 S 16 C 10 S 19

    public static Canvas2 createCanvas(String line,double maxArea) throws IrregularCanvasException {
       String [] parts = line.split("\\s+");
       String id = parts[0];
       List<Shape2> shapeList = new ArrayList<>();
       for (int i=1;i<parts.length;i+=2)
       {
          Shape2 s = Shape2.createShape(Integer.parseInt(parts[i+1]),parts[i].charAt(0),maxArea);
          if (s.area()>maxArea)
              throw new IrregularCanvasException(id,maxArea);
           shapeList.add(s);
       }
       return new Canvas2(id,shapeList);
    }

    //ID total_shapes total_circles total_squares min_area max_area average_area
    @Override
    public String toString() {
        DoubleSummaryStatistics dss = shapes.stream().mapToDouble(Shape2::area).summaryStatistics();
        return String.format("%s %d %d %d %.2f %.2f %.2f",id,
                shapes.size(),
                totalCircles(),
                shapes.size() - totalCircles(),
                dss.getMin(),
                dss.getMax(),
                dss.getAverage());
    }
    public double areaSum()
    {
        return shapes.stream().mapToDouble(Shape2::area).sum();
    }

    @Override
    public int compareTo(Canvas2 o) {
        return -Double.compare(this.areaSum(),o.areaSum());
    }
}
class ShapesApplication2
{
    List<Canvas2> canvas;
    double maxArea;

    public ShapesApplication2(double maxArea) {
        this.maxArea = maxArea;
        this.canvas = new ArrayList<>();
    }

    public void readCanvases(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = br.readLine())!=null)
        {
            try {
                canvas.add(Canvas2.createCanvas(line,maxArea));
            }
            catch (IrregularCanvasException e)
            {
                System.out.println(e.getMessage());
            }
        }
    }

    public void printCanvases(PrintStream out) {
        PrintWriter pw = new PrintWriter(out);
        Collections.sort(canvas);
        for (Canvas2 canvas2: canvas)
        {
            pw.println(canvas2.toString());
        }
        pw.flush();
    }
}

public class Shapes2Test {

    public static void main(String[] args) throws IOException {

        ShapesApplication2 shapesApplication = new ShapesApplication2(10000);

        System.out.println("===READING CANVASES AND SHAPES FROM INPUT STREAM===");
        shapesApplication.readCanvases(System.in);

        System.out.println("===PRINTING SORTED CANVASES TO OUTPUT STREAM===");
        shapesApplication.printCanvases(System.out);

    }
}
