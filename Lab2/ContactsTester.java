package Lab2;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

abstract class Contact
{
    private String date;

    public Contact(String date) {
        this.date = date;
    }
    public boolean isNewerThan(Contact c) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD");
        Date first = sdf.parse(this.date);
        Date second = sdf.parse(c.date);
        if (first.after(second))
            return true;
        return false;
    }
    /*private int day;
    private int month;
    private int year;

    public Contact(String date) {
        String [] niza = date.split("-");
        this.year = Integer.parseInt(niza[0]);
        this.month = Integer.parseInt(niza[1]);
        this.day = Integer.parseInt(niza[2]);
    }
    public boolean isNewerThan(Contact c)
    {
        if (this.year>c.year) return true;
        if (this.year == c.year&&this.month>c.month) return true;
        if (this.year==c.year && this.month==c.month && this.day>c.day) return true;
        return false;
    }*/
    abstract public String getType();
}
class EmailContact extends Contact
{
    private String email;

    public EmailContact(String date,String email) {
        super(date);
        this.email = email;
    }

    @Override
    public String getType() {
        return "Email";
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return String.format("\"%s\"",email);
    }
}
class PhoneContact extends Contact
{
    private String phone;
    enum Operator {VIP, ONE, TMOBILE};

    public PhoneContact(String date,String phone) {
        super(date);
        this.phone = phone;
    }

    public Operator getOperator()
    {
        char sign = phone.charAt(2);
        if (sign == '0' || sign=='1' || sign == '2') return Operator.TMOBILE;
        if (sign == '5' || sign == '6') return Operator.ONE;
        return Operator.VIP;
    }

    @Override
    public String getType() {
        return "Phone";
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return String.format("\"%s\"",phone);
    }
}
class Student
{
    private String firstName;
    private String lastName;
    private String city;
    private int age;
    private long index;
    private List<Contact> contacts;

    public Student(String firstName, String lastName, String city, int age, long index) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.age = age;
        this.index = index;
        this.contacts = new ArrayList<>();
    }
    public void addEmailContact(String date, String email)
    {
        contacts.add(new EmailContact(date,email));
    }
    public void addPhoneContact(String date, String phone)
    {
        contacts.add(new PhoneContact(date,phone));
    }
    public Contact [] getEmailContacts()
    {
        return contacts.stream().filter(contact -> contact.getType().equals("Email")).toArray(Contact[]::new);
    }
    public Contact [] getPhoneContacts()
    {
        return contacts.stream().filter(contact -> contact.getType().equals("Phone")).toArray(Contact []::new);
    }

    public String getCity() {
        return city;
    }
    public String getFullName()
    {
        return firstName + lastName;
    }
    public int numOfContacts()
    {
        return contacts.size();
    }

    public long getIndex() {
        return index;
    }
    public Contact getLatestContact()
    {
        return contacts.stream().reduce(((contact, contact2) -> {
            try {
                return (contact.isNewerThan(contact2) ? contact:contact2);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        })).get();
    }

    @Override
    public String toString() {
        String phoneContacts = Arrays.stream(getPhoneContacts()).map(contact -> contact.toString()).collect(Collectors.joining(", "));
        String emailContacts = Arrays.stream(getEmailContacts()).map(contact -> contact.toString()).collect(Collectors.joining(", "));
        return String.format("{\"ime\":\"%s\", \"prezime\":\"%s\", \"vozrast\":%d, \"grad\":\"%s\", \"indeks\":%d, \"telefonskiKontakti\":[%s], \"emailKontakti\":[%s]}",
                firstName,lastName,age,city,index,phoneContacts,emailContacts);
    }
}
class Faculty
{
    private String name;
    private List<Student> students;

    public Faculty(String name, Student [] students) {
        this.name = name;
        this.students = new ArrayList<>();
        this.students.addAll(Arrays.stream(students).collect(Collectors.toList()));
    }
    public int countStudentsFromCity(String cityName)
    {
        return (int) students.stream().filter(student -> student.getCity().equals(cityName)).count();
    }
    public Student getStudent(long index)
    {
        return students.stream().filter(student -> student.getIndex() == index).findFirst().get();
    }
    public double getAverageNumberOfContacts()
    {
        //return students.stream().mapToDouble(Student::numOfContacts).sum()/students.size();
        return students.stream().mapToDouble(Student::numOfContacts).average().getAsDouble();
    }
    public Student getStudentWithMostContacts()
    {
        return students.stream().reduce(((student, student2) -> student.numOfContacts()>student2.numOfContacts()? student: (student.numOfContacts() == student2.numOfContacts()
                ? (student.getIndex()>student2.getIndex() ? student:student2): student2))).get();
    }

    @Override
    public String toString() {
        return String.format("{\"fakultet\":\"%s\", \"studenti\":%s}",name,students.toString());
    }
}

public class ContactsTester {

    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);

        int tests = scanner.nextInt();
        Faculty faculty = null;

        int rvalue = 0;
        long rindex = -1;

        DecimalFormat df = new DecimalFormat("0.00");

        for (int t = 0; t < tests; t++) {

            rvalue++;
            String operation = scanner.next();

            switch (operation) {
                case "CREATE_FACULTY": {
                    String name = scanner.nextLine().trim();
                    int N = scanner.nextInt();

                    Student[] students = new Student[N];

                    for (int i = 0; i < N; i++) {
                        rvalue++;

                        String firstName = scanner.next();
                        String lastName = scanner.next();
                        String city = scanner.next();
                        int age = scanner.nextInt();
                        long index = scanner.nextLong();

                        if ((rindex == -1) || (rvalue % 13 == 0))
                            rindex = index;

                        Student student = new Student(firstName, lastName, city,
                                age, index);
                        students[i] = student;
                    }

                    faculty = new Faculty(name, students);
                    break;
                }

                case "ADD_EMAIL_CONTACT": {
                    long index = scanner.nextInt();
                    String date = scanner.next();
                    String email = scanner.next();

                    rvalue++;

                    if ((rindex == -1) || (rvalue % 3 == 0))
                        rindex = index;

                    faculty.getStudent(index).addEmailContact(date, email);
                    break;
                }

                case "ADD_PHONE_CONTACT": {
                    long index = scanner.nextInt();
                    String date = scanner.next();
                    String phone = scanner.next();

                    rvalue++;

                    if ((rindex == -1) || (rvalue % 3 == 0))
                        rindex = index;

                    faculty.getStudent(index).addPhoneContact(date, phone);
                    break;
                }

                case "CHECK_SIMPLE": {
                    System.out.println("Average number of contacts: "
                            + df.format(faculty.getAverageNumberOfContacts()));

                    rvalue++;

                    String city = faculty.getStudent(rindex).getCity();
                    System.out.println("Number of students from " + city + ": "
                            + faculty.countStudentsFromCity(city));

                    break;
                }

                case "CHECK_DATES": {

                    rvalue++;

                    System.out.print("Latest contact: ");
                    Contact latestContact = faculty.getStudent(rindex)
                            .getLatestContact();
                    if (latestContact.getType().equals("Email"))
                        System.out.println(((EmailContact) latestContact)
                                .getEmail());
                    if (latestContact.getType().equals("Phone"))
                        System.out.println(((PhoneContact) latestContact)
                                .getPhone()
                                + " ("
                                + ((PhoneContact) latestContact).getOperator()
                                .toString() + ")");

                    if (faculty.getStudent(rindex).getEmailContacts().length > 0
                            && faculty.getStudent(rindex).getPhoneContacts().length > 0) {
                        System.out.print("Number of email and phone contacts: ");
                        System.out
                                .println(faculty.getStudent(rindex)
                                        .getEmailContacts().length
                                        + " "
                                        + faculty.getStudent(rindex)
                                        .getPhoneContacts().length);

                        System.out.print("Comparing dates: ");
                        int posEmail = rvalue
                                % faculty.getStudent(rindex).getEmailContacts().length;
                        int posPhone = rvalue
                                % faculty.getStudent(rindex).getPhoneContacts().length;

                        System.out.println(faculty.getStudent(rindex)
                                .getEmailContacts()[posEmail].isNewerThan(faculty
                                .getStudent(rindex).getPhoneContacts()[posPhone]));
                    }

                    break;
                }

                case "PRINT_FACULTY_METHODS": {
                    System.out.println("Faculty: " + faculty.toString());
                    System.out.println("Student with most contacts: "
                            + faculty.getStudentWithMostContacts().toString());
                    break;
                }

            }

        }

        scanner.close();
    }
}
