package VtorKolokvium;
import java.util.*;
import java.util.stream.Collectors;

class Movie implements Comparable<Movie>
{
    private String title;
    private int [] ratings;

    public Movie(String title, int[] ratings) {
        this.title = title;
        this.ratings = ratings;
    }

    public String getTitle() {
        return title;
    }
    public double ratingCoef(int maxRatings)
    {
        return avgRating() * ratings.length / maxRatings;
    }

    public int sizeRatings()
    {
        return ratings.length;
    }

    public double avgRating()
    {
        return Arrays.stream(ratings).average().getAsDouble();
    }
    public static Comparator<Movie> byAvgRatingAndTitle()
    {
        return Comparator.comparing(Movie::avgRating).reversed().thenComparing(Movie::getTitle);
    }


    @Override
    public String toString() {
        return String.format("%s (%.2f) of %d ratings",
                title,avgRating(),ratings.length);
    }


    @Override
    public int compareTo(Movie o) {
        if (this.ratingCoef(MoviesList.maxRatings) == o.ratingCoef(MoviesList.maxRatings))
        {
            return this.title.compareTo(o.title);
        }
        return -Double.compare(this.ratingCoef(MoviesList.maxRatings),o.ratingCoef(MoviesList.maxRatings));
    }
}
class MoviesList
{
    private List<Movie> movies;
    static int maxRatings;

    public MoviesList()
    {
        this.movies = new ArrayList<>();
    }

    public void addMovie(String title, int[] ratings) {
        movies.add(new Movie(title,ratings));
    }

    public List<Movie> top10ByAvgRating() {
        return movies.stream()
                .sorted(Movie.byAvgRatingAndTitle())
                .limit(10)
                .collect(Collectors.toList());

    }

    public List<Movie> top10ByRatingCoef() {
        maxRatings = movies.stream().mapToInt(Movie::sizeRatings).max().getAsInt();
        return movies.stream()
                .sorted(Movie::compareTo)
                .limit(10)
                .collect(Collectors.toList());
    }
}

public class MoviesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MoviesList moviesList = new MoviesList();
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int x = scanner.nextInt();
            int[] ratings = new int[x];
            for (int j = 0; j < x; ++j) {
                ratings[j] = scanner.nextInt();
            }
            scanner.nextLine();
            moviesList.addMovie(title, ratings);
        }
        scanner.close();
        List<Movie> movies = moviesList.top10ByAvgRating();
        System.out.println("=== TOP 10 BY AVERAGE RATING ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
        movies = moviesList.top10ByRatingCoef();
        System.out.println("=== TOP 10 BY RATING COEFFICIENT ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
    }
}

// vashiot kod ovde

