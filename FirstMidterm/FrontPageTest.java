package PrvKolokvium;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

class Category
{
    private String name;

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

abstract class NewsItem
{
    private String title;
    private Date publishedDate;
    private Category category;

    public NewsItem(String title, Date publishedDate, Category category) {
        this.title = title;
        this.publishedDate = publishedDate;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public Category getCategory() {
        return category;
    }

    public abstract String getTeaser();
}

class TextNewsItem extends NewsItem
{
    private String text;

    public TextNewsItem(String title, Date publishedDate, Category category, String text) {
        super(title, publishedDate, category);
        this.text = text;
    }

    @Override
    public String getTeaser() {
        Date date = new Date();
        int minutes = (int) ((date.getTime() - getPublishedDate().getTime())/ TimeUnit.MINUTES.toMillis(1));
        return String.format("%s\n%d\n%s\n",getTitle()
                ,minutes,text.length()<80 ? text:text.substring(0,80));
    }

    @Override
    public String toString() {
        return getTeaser();
    }
}
class MediaNewsItem extends NewsItem
{
    private String url;
    private int views;

    public MediaNewsItem(String title, Date publishedDate, Category category,String url,int views) {
        super(title, publishedDate, category);
        this.url = url;
        this.views = views;
    }

    @Override
    public String getTeaser() {
        Date date = new Date();
        int minutes = (int) ((date.getTime() - getPublishedDate().getTime())/60000);
        return String.format("%s\n%d\n%s\n%d\n",
                getTitle(),
                minutes,
                url,
                views);
    }

    @Override
    public String toString() {
        return getTeaser();
    }
}

class FrontPage
{
    private List<NewsItem> newsItems;
    private Category [] categories;

    public FrontPage(Category [] categories)
    {
        this.newsItems = new ArrayList<>();
        this.categories = categories;
    }

    public void addNewsItem(NewsItem newsItem) {
        newsItems.add(newsItem);
    }

    public List<NewsItem> listByCategory(Category category) {
        return newsItems.stream()
                .filter(c -> c.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public List<NewsItem> listByCategoryName(String category) throws CategoryNotFoundException {
        if (Arrays.stream(categories).noneMatch(c->c.getName().equals(category)))
        {
            throw new CategoryNotFoundException("Category " + category + " was not found");
        }
        return newsItems.stream()
                .filter(c->c.getCategory().getName().equals(category))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (NewsItem newsItem: newsItems)
        {
            sb.append(newsItem.getTeaser());
        }
        return sb.toString();
    }
}
class CategoryNotFoundException extends Exception
{
    public CategoryNotFoundException(String message)
    {
        super(message);
    }
}

public class FrontPageTest {
    public static void main(String[] args) {
        // Reading
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] parts = line.split(" ");
        Category[] categories = new Category[parts.length];
        for (int i = 0; i < categories.length; ++i) {
            categories[i] = new Category(parts[i]);
        }
        int n = scanner.nextInt();
        scanner.nextLine();
        FrontPage frontPage = new FrontPage(categories);
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            cal = Calendar.getInstance();
            int min = scanner.nextInt();
            cal.add(Calendar.MINUTE, -min);
            Date date = cal.getTime();
            scanner.nextLine();
            String text = scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();
            TextNewsItem tni = new TextNewsItem(title, date, categories[categoryIndex], text);
            frontPage.addNewsItem(tni);
        }

        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int min = scanner.nextInt();
            cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -min);
            scanner.nextLine();
            Date date = cal.getTime();
            String url = scanner.nextLine();
            int views = scanner.nextInt();
            scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();
            MediaNewsItem mni = new MediaNewsItem(title, date, categories[categoryIndex], url, views);
            frontPage.addNewsItem(mni);
        }
        // Execution
        String category = scanner.nextLine();
        System.out.println(frontPage);
        for(Category c : categories) {
            System.out.println(frontPage.listByCategory(c).size());
        }
        try {
            System.out.println(frontPage.listByCategoryName(category).size());
        } catch(CategoryNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}

