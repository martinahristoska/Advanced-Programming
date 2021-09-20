package PrvKolokvium;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


class Transaction implements Comparable<Transaction>
{
    private int year;
    private int month;
    private int day;
    private double value;
    private String currency;

    public Transaction(int year, int month, int day, double value, String currency) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.value = value;
        this.currency = currency;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public double getValue() {
        return value;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public int compareTo(Transaction o) {
        return Comparator.comparing(Transaction::getYear)
                .thenComparing(Transaction::getMonth)
                .thenComparing(Transaction::getDay)
                .compare(this,o);
    }

    @Override
    public String toString() {
        return String.format("%04d/%02d/%02d %10.2f %s", year,month,day,value,currency);
    }
    public double toMKD()
    {
        if (currency.equalsIgnoreCase("EUR"))
            return value*Constants.EUR;
        else if (currency.equalsIgnoreCase("USD"))
            return value * Constants.USD;
        else
            return value;
    }
}
class Transactions
{
    List<Transaction>  transactions;

    public Transactions()
    {
        this.transactions = new ArrayList<>();
    }

    public List<String> readData(InputStream in) throws IOException {
        List<String> badDates = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = br.readLine())!=null)
        {
            String [] parts = line.split(";");
            int value = Integer.parseInt(parts[1]);
            String currency = parts[2];
            String [] date = parts[0].split("/");
            int year = Integer.parseInt(date[0]);
            int month = Integer.parseInt(date[1]);
            int day = Integer.parseInt(date[2]);

            if (!validDate(year,month,day))
                badDates.add(parts[0]);
            else {
                transactions.add(new Transaction(year,month,day,value,currency));
            }
        }
        return badDates;
    }
    public boolean isLeapYear(int year)
    {
        return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
    }

    public boolean validDate(int year,int month,int day)
    {
        if (month>12)
            return false;
        if (isLeapYear(year) && month==2 && day>29)
            return false;
        if (day > Constants.DAYS[month-1])
            return false;
        return true;
    }

    public void writeData(int year, PrintStream out) {
        PrintWriter pw = new PrintWriter(out);
        List<Transaction> filtered = transactions.stream()
                .sorted()
                .filter(y -> y.getYear() == year)
                .collect(Collectors.toList());
        filtered.forEach(pw::println);
        double total = filtered.stream()
                .mapToDouble(Transaction::toMKD)
                .sum();
        System.out.printf("Balance: %10d MKD\n",Math.round(total));
    }
}
public class TransactionsTest {
    public static void main (String [] args) throws IOException {
        Transactions transactions = new Transactions();
        System.out.println(transactions.getClass());
        List<String> invalid = transactions.readData(System.in);
        System.out.println("=== INVALID DATES ===");
        for (String s : invalid) {
            System.out.println(s);
        }
        System.out.println("=== TRANSACTIONS 2001 ===");
        transactions.writeData(2001, System.out);
        System.out.println("=== TRANSACTIONS 2007 ===");
        transactions.writeData(2007, System.out);
        System.out.println("=== TRANSACTIONS 2013 ===");
        transactions.writeData(2013, System.out);
    }
}

class Constants {
    static final float USD = 50;
    static final float EUR = 61.5f;
    static final int [] DAYS = {
            31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
    };
}
