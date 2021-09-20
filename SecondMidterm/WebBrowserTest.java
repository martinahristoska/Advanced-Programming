package VtorKolokvium;

import java.util.Comparator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

class Tab implements Comparable<Tab>
{
    private String title;
    private int memory;

    public Tab(String title, int memory) {
        this.title = title;
        this.memory = memory;
    }

    public String getTitle() {
        return title;
    }

    public int getMemory() {
        return memory;
    }

    @Override
    public int compareTo(Tab o) {
        return Comparator.comparing(Tab::getMemory).reversed()
                .thenComparing(Tab::getTitle)
                .compare(this,o);
    }

    @Override
    public String toString() {
        return String.format("(%dMB) %s",memory,title);
    }
}

class WebBrowser
{
    int limit;
    TreeSet<Tab> tabs;

    public WebBrowser(int limit) {
        this.limit = limit;
        this.tabs = new TreeSet<>();
    }

    public void addTab(String tab, int memory) {
        if (memory>limit)
            return;
        while (memory > getFreeMemory())
            tabs.pollFirst();

        tabs.add(new Tab(tab,memory));

    }
    public int getFreeMemory()
    {
        return limit - tabs.stream().mapToInt(Tab::getMemory).sum();
    }

    @Override
    public String toString() {
        String s = tabs.stream().map(Tab::toString).collect(Collectors.joining("\n"));
        s+="\nTotal memory: " + (limit-getFreeMemory()) + "MB";
        return s;
    }
}

public class WebBrowserTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int limit = scanner.nextInt();
        scanner.nextLine();
        WebBrowser webBrowser = new WebBrowser(limit);
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; i++) {
            String tab = scanner.nextLine();
            int memory = scanner.nextInt();
            scanner.nextLine();
            webBrowser.addTab(tab, memory);
        }
        System.out.println(webBrowser);
        scanner.close();
    }
}
