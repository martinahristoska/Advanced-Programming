/*package IspitniZadaci;
import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class TextProcessor
{
    List<String> text;

    public TextProcessor() {
        this.text = new ArrayList<>();
    }

    public void readText(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        text = br.lines().map(line -> line.replaceAll("[^A-Za-z\\s+]",""))
                .map(line -> Arrays.stream(line.split("\\s+"))
                .max(Comparator.comparing(String::length).thenComparing(Function.identity()))
                .orElse("none"))
                .collect(Collectors.toList());

    }

    public void printProcessedText(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        text.stream().forEach(pw::println);
        pw.flush();
    }
}

public class TextProcessorTest1 {
    public static void main(String[] args) {
        TextProcessor textProcessor = new TextProcessor();

        textProcessor.readText(System.in);

        textProcessor.printProcessedText(System.out);
    }
}*/
