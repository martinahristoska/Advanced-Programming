package VtorKolokvium;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

class Team
{
    private String name;
    private int allGames;
    private int wins;
    private int draws;
    private int loses;
    private int difference;

    public Team(String name) {
        this.name = name;
        this.draws = 0;
        this.wins = 0;
        this.allGames = 0;
        this.difference = 0;
        this.loses = 0;
    }

    public int allPoints()
    {
        return wins*3 + draws;
    }

    public int getDifference() {
        return difference;
    }

    @Override
    public String toString() {
        return String.format("%-15s%5d%5d%5d%5d%5d",name,allGames,
                wins,draws,loses,allPoints());
    }
    public void updateTable(int homeGoals,int awayGoals)
    {
        this.allGames++;
        int result = homeGoals - awayGoals;
        if (result>0)
        {
            this.wins++;
        }
        else if (result==0)
        {
            this.draws++;
        }
        else {
            this.loses++;
        }
        this.difference+=result;
    }
}

class FootballTable
{
    Map<String,Team> teamMap;

    public FootballTable()
    {
        this.teamMap = new TreeMap<>();
    }

    public void addGame(String homeTeam, String awayTeam, int homeGoals, int awayGoals) {
        teamMap.putIfAbsent(homeTeam,new Team(homeTeam));
        teamMap.putIfAbsent(awayTeam, new Team(awayTeam));

        teamMap.get(homeTeam).updateTable(homeGoals,awayGoals);
        teamMap.get(awayTeam).updateTable(awayGoals,homeGoals);
    }

    public void printTable() {
        List<Team> list = teamMap.values().stream()
                .sorted(Comparator.comparing(Team::allPoints)
                .thenComparing(Team::getDifference).reversed())
                .collect(Collectors.toList());
        list.forEach(team -> System.out.printf("%2d. %s\n", list.indexOf(team)+1,team));
    }
}

public class FootballTableTest {
    public static void main(String[] args) throws IOException {
        FootballTable table = new FootballTable();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.lines()
                .map(line -> line.split(";"))
                .forEach(parts -> table.addGame(parts[0], parts[1],
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3])));
        reader.close();
        System.out.println("=== TABLE ===");
        System.out.printf("%-19s%5s%5s%5s%5s%5s\n", "Team", "P", "W", "D", "L", "PTS");
        table.printTable();
    }
}

