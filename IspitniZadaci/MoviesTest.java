package IspitniZadaci;
import java.util.*;
import java.util.stream.Collectors;

class Movie
{
    private String title;
    private String director;
    private String genre;
    private float rating;

    public Movie(String title, String director, String genre, float rating) {
        this.title = title;
        this.director = director;
        this.genre = genre;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public String getGenre() {
        return genre;
    }

    public float getRating() {
        return rating;
    }
    public static Comparator<Movie> byTitle = Comparator.comparing(Movie::getTitle)
            .thenComparing(Movie::getRating);
    public static Comparator<Movie> byRating = Comparator.comparing(Movie::getRating)
            .thenComparing(Movie::getTitle);
    @Override
    public String toString() {
        return String.format("%s (%s, %s) %.2f", title, director, genre, rating);
    }
}
class MoviesCollection
{
    List<Movie> movies;

    public MoviesCollection()
    {
        this.movies = new ArrayList<>();
    }

    public void addMovie(Movie movie) {
        movies.add(movie);
    }

    public void printByGenre(String genre) {
        movies.stream()
                .filter(m->m.getGenre().equalsIgnoreCase(genre))
                .sorted(Movie.byTitle)
                .forEach(System.out::println);
    }

    public List<Movie> getTopRatedN(int n) {
        if (movies.size()<n)
            return movies;
        return movies.stream()
                .sorted(Movie.byRating)
                .limit(n)
                .collect(Collectors.toList());
    }

    public Map<String, Integer> getCountByDirector() {
        return movies.stream()
                .collect(Collectors.toMap(
                        Movie::getDirector,
                        movie -> 1,
                        (l1,l2) ->{
                            l1+=l2;
                            return l1;
                        },
                        TreeMap::new
                ));
    }
}

public class MoviesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int printN = scanner.nextInt();
        scanner.nextLine();
        MoviesCollection moviesCollection = new MoviesCollection();
        Set<String> genres = fillCollection(scanner, moviesCollection);
        System.out.println("=== PRINT BY GENRE ===");
        for (String genre : genres) {
            System.out.println("GENRE: " + genre);
            moviesCollection.printByGenre(genre);
        }
        System.out.println("=== TOP N BY RATING ===");
        printList(moviesCollection.getTopRatedN(printN));

        System.out.println("=== COUNT BY DIRECTOR ===");
        printMap(moviesCollection.getCountByDirector());
    }

    static void printMap(Map<String,Integer> countByDirector) {
        countByDirector.entrySet().stream()
                .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
    }

    static void printList(List<Movie> movies) {
        for (Movie movie : movies) {
            System.out.println(movie);
        }
    }

    static TreeSet<String> fillCollection(Scanner scanner, MoviesCollection collection) {
        TreeSet<String> categories = new TreeSet<String>();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            Movie movie = new Movie(parts[0], parts[1], parts[2], Float.parseFloat(parts[3]));
            collection.addMovie(movie);
            categories.add(parts[2]);
        }
        return categories;
    }
}
