package PrvKolokvium;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Rectangles implements Comparable<Rectangles>
{
    private int size;

    public Rectangles(int size) {
        this.size = size;
    }

    public int perimeter()
    {
        return size*4;
    }

    @Override
    public int compareTo(Rectangles o) {
        return Integer.compare(this.perimeter(),o.perimeter());
    }
}

class Canvases
{
    private String id;
    List<Rectangles> rectangles;
    static int count = 0;

    public Canvases(String id, List<Rectangles> rectangles) {
        this.id = id;
        this.rectangles = rectangles;
    }

    public static Canvases createCanvas(String line)
    {
        String [] parts = line.split("\\s+");
        String id = parts[0];
        List<Rectangles> rectanglesList = new ArrayList<>();
        for (int i=1;i<parts.length;i++)
        {
            rectanglesList.add(new Rectangles(Integer.parseInt(parts[i])));
        }
        count+=rectanglesList.size();
        Canvases canvases = new Canvases(id,rectanglesList);
        return canvases;
    }

    public int squareCount()
    {
        return rectangles.size();
    }

    public int totalSquaresPerimeter()
    {
        return rectangles.stream()
                .mapToInt(Rectangles::perimeter)
                .sum();
    }

    @Override
    public String toString() {
        return String.format("%s %d %d",id,squareCount(),totalSquaresPerimeter());
    }
}
class ShapesApplication
{
    List<Canvases> canvases;

    public ShapesApplication()
    {
        this.canvases = new ArrayList<>();
    }

    /*public int readCanvases(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        List<Rectangles> rectanglesList = new ArrayList<>();
        while ((line = br.readLine())!=null)
        {
            String [] parts = line.split("\\s+");
            String id = parts[0];
            for (int i=1;i<parts.length;i++)
            {
                rectanglesList.add(new Rectangles(Integer.parseInt(parts[i])));
            }
            canvases.add(new Canvases(id,rectanglesList));
        }
        return rectanglesList.size();
    }*/
    public int readCanvases(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = br.readLine())!=null)
        {
            canvases.add(Canvases.createCanvas(line));
        }
        return Canvases.count;
    }

    public int MaxSquaresPerimeter()
    {
        return canvases.stream()
                .mapToInt(Canvases::totalSquaresPerimeter)
                .max()
                .getAsInt();
    }


    public void printLargestCanvasTo(PrintStream out) {
        PrintWriter pw = new PrintWriter(out);
        List<Canvases> printList = new ArrayList<>();
        printList = canvases.stream()
                .filter(p->p.totalSquaresPerimeter()==MaxSquaresPerimeter())
                .collect(Collectors.toList());
        printList.forEach(System.out::println);
    }
}

public class Shapes1Test {

    public static void main(String[] args) throws IOException {
        ShapesApplication shapesApplication = new ShapesApplication();

        System.out.println("===READING SQUARES FROM INPUT STREAM===");
        System.out.println(shapesApplication.readCanvases(System.in));
        System.out.println("===PRINTING LARGEST CANVAS TO OUTPUT STREAM===");
        shapesApplication.printLargestCanvasTo(System.out);

    }
}