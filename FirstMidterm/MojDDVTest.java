package PrvKolokvium;

import java.io.*;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

class AmountNotAllowedException extends Exception
{
    int price;
    public AmountNotAllowedException(int price)
    {
        this.price = price;
    }

    @Override
    public String getMessage() {
        return String.format("Receipt with amount %d is not allowed to be scanned",price);
    }
}

class Item
{
    private char type;
    private int price;

    public Item(char type, int price) {
        this.type = type;
        this.price = price;
    }

    public double getTax()
    {
        switch (type)
        {
            case 'A' : return price*0.18;
            case 'B' : return price * 0.05;
            case 'V' : return price * 0.0;
            default: return 0;
        }
    }
    public double getTaxReturn()
    {
        return getTax()*0.15;
    }

    public char getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }
}
class Receipt
{
    private String id;
    private List<Item> items;

    public Receipt(String id, List<Item> items) {
        this.id = id;
        this.items = items;
    }
    public int sumOfAmounts()
    {
        return items.stream().mapToInt(Item::getPrice).sum();
    }

    public double taxReturn()
    {
        return items.stream().mapToDouble(Item::getTaxReturn).sum();
    }

    @Override
    public String toString() {
        return String.format("%10s\t%10d\t%10.5f",id,sumOfAmounts(),taxReturn());
    }
    public static Receipt createReceipt(String line) throws AmountNotAllowedException {
        String [] parts = line.split("\\s+");
        String id = parts[0];
        List<Item> items = new ArrayList<>();
        for (int i=1;i<parts.length;i+=2)
        {
            int price = Integer.parseInt(parts[i]);
            char type = parts[i+1].charAt(0);
            items.add(new Item(type,price));
        }
        Receipt receipt = new Receipt(id,items);
        if (receipt.sumOfAmounts()>30000)
            throw new AmountNotAllowedException(receipt.sumOfAmounts());
        return receipt;
    }
}
class MojDDV
{
    List<Receipt> receipts;

    public MojDDV()
    {
        this.receipts = new ArrayList<>();
    }

    public void readRecords(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = br.readLine())!=null)
        {
            try {
                receipts.add(Receipt.createReceipt(line));
            }
            catch (AmountNotAllowedException e)
            {
                System.out.println(e.getMessage());
            }
        }
    }

    public void printTaxReturns(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        for (Receipt receipt: receipts)
        {
            pw.println(receipt.toString());
        }
        pw.flush();
    }

    public void printStatistics(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        DoubleSummaryStatistics dss =
                receipts.stream().mapToDouble(Receipt::taxReturn).summaryStatistics();
        pw.printf("min:\t%5.3f\nmax:\t%5.3f\nsum:\t%5.3f\ncount:\t%-5d\navg:\t%5.3f\n",
                dss.getMin(),
                dss.getMax(),
                dss.getSum(),
                dss.getCount(),
                dss.getAverage());
        pw.flush();
    }
}

public class MojDDVTest {

    public static void main(String[] args) throws IOException {

        MojDDV mojDDV = new MojDDV();

        System.out.println("===READING RECORDS FROM INPUT STREAM===");
        mojDDV.readRecords(System.in);

        System.out.println("===PRINTING TAX RETURNS RECORDS TO OUTPUT STREAM ===");
        mojDDV.printTaxReturns(System.out);

        System.out.println("===PRINTING SUMMARY STATISTICS FOR TAX RETURNS TO OUTPUT STREAM===");
        mojDDV.printStatistics(System.out);


    }
}
