package IspitniZadaci;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class PartitionDoesNotExistException extends Exception
{
    public PartitionDoesNotExistException(String topic,Integer partition)
    {
        super(String.format("The topic %s does not have a partition with number %d", topic, partition));
    }
}
class UnsupportedOperationException extends Exception{
    public UnsupportedOperationException(String message) {
        super(message);
    }
}

class PartitionAssigner {
    public static Integer assignPartition (Message message, int partitionsCount) {
        return (Math.abs(message.getKey().hashCode())  % partitionsCount) + 1;
    }
}

class Message implements Comparable<Message>
{
    private LocalDateTime timestamp;
    private String text;
    private Integer partition;
    private String key;

    public Message(LocalDateTime timestamp, String text, Integer partition, String key) {
        this.timestamp = timestamp;
        this.text = text;
        this.partition = partition;
        this.key = key;
    }

    public Message(LocalDateTime timestamp, String text, String key) {
        this.timestamp = timestamp;
        this.text = text;
        this.key = key;
    }

    @Override
    public String toString() {
        return "Message{" +
                "timestamp=" + timestamp +
                ", message='" + text + '\'' +
                '}';
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getText() {
        return text;
    }

    public Integer getPartition() {
        return partition;
    }

    public String getKey() {
        return key;
    }

    @Override
    public int compareTo(Message o) {
        return Comparator.comparing(Message::getTimestamp)
                .compare(this,o);
    }
}
class Topic
{
    String topicName;
    int partitionsCount;
    Map<Integer,TreeSet<Message>> messageByPartition;

    public Topic(String topicName, int partitionsCount) {
        this.topicName = topicName;
        this.partitionsCount = partitionsCount;
        this.messageByPartition = new TreeMap<>();
        IntStream.range(1,partitionsCount+1)
                .forEach(i->messageByPartition.put(i,new TreeSet<>()));
    }

    public void addMessage(Message message) throws PartitionDoesNotExistException {
        Integer messagePartition = message.getPartition();
        if (messagePartition==null)
        {
            messagePartition = PartitionAssigner.assignPartition(message,partitionsCount);
        }
        if (!messageByPartition.containsKey(messagePartition))
        {
            throw new PartitionDoesNotExistException(topicName,messagePartition);
        }

        messageByPartition.computeIfPresent(messagePartition, (k,v) ->
        {
            if (v.size() == MessageBroker.capacityPerTopic)
                v.remove(v.first());
                v.add(message);
                return v;
        });
    }
    void changeNumberOfPartitions (int newPartitionsNumber) throws UnsupportedOperationException
    {
        if (newPartitionsNumber<partitionsCount)
        {
            throw new UnsupportedOperationException("Partitions number cannot be decreased!");
        }
        int difference = newPartitionsNumber - partitionsCount;
        int size = messageByPartition.size();
        IntStream.range(1,difference+1)
                .forEach(i->messageByPartition.putIfAbsent(size+i,new TreeSet<>()));
        this.partitionsCount = newPartitionsNumber;
    }
    /*
    Broker with  1 topics:
Topic:     topic1 Partitions:     6
 1 : Count of messages:     4
Messages:
Message{timestamp=2018-03-13T16:55, message='Message from the system with id16574'}
Message{timestamp=2018-05-23T08:40, message='Message from the system with id11249'}
Message{timestamp=2018-07-07T04:06, message='Message from the system with id19472'}
Message{timestamp=2018-07-13T17:48, message='Message from the system with id19979'}
     */

    @Override
    public String toString() {
        return String.format("Topic: %10s Partitions: %5d\n%s",
                topicName,
                partitionsCount,
                messageByPartition.entrySet().stream()
                .map(entry -> String.format("%2d : Count of messages: %5d\n%s",
                        entry.getKey(),
                        entry.getValue().size(),
                        !entry.getValue().isEmpty() ? "Messages:\n" +
                        entry.getValue()
                        .stream()
                        .map(Message::toString)
                        .collect(Collectors.joining("\n")) : ""))
        .collect(Collectors.joining("\n")));
    }
}
class MessageBroker
{
   public static LocalDateTime minimumDate;
   public static Integer capacityPerTopic;
   Map<String,Topic> topicMap; //topic spored imeto

    public MessageBroker(LocalDateTime minimumDate,Integer capacityPerTopic) {
        MessageBroker.minimumDate = minimumDate;
        MessageBroker.capacityPerTopic = capacityPerTopic;
        this.topicMap = new TreeMap<>();
    }

    public void addTopic(String topicName, int partitionsCount) {
        topicMap.put(topicName,new Topic(topicName,partitionsCount));
    }

    public void addMessage(String topic, Message message) throws PartitionDoesNotExistException,UnsupportedOperationException {
        if (message.getTimestamp().isBefore(minimumDate))
        {
            return;
        }
        topicMap.get(topic).addMessage(message);
    }

    public void changeTopicSettings(String topicName, Integer partitions) throws UnsupportedOperationException {
        topicMap.get(topicName).changeNumberOfPartitions(partitions);
    }
    //Broker with  1 topics:
    @Override
    public String toString() {
        return String.format("Broker with %2d topics:\n%s",
                topicMap.size(),
                topicMap.values().stream()
                        .map(Topic::toString)
                        .collect(Collectors.joining("\n")));
    }
}



public class MessageBrokersTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String date = sc.nextLine();
        LocalDateTime localDateTime = LocalDateTime.parse(date);
        Integer partitionsLimit = Integer.parseInt(sc.nextLine());
        MessageBroker broker = new MessageBroker(localDateTime, partitionsLimit);
        int topicsCount = Integer.parseInt(sc.nextLine());

        //Adding topics
        for (int i=0;i<topicsCount;i++) {
            String line = sc.nextLine();
            String [] parts = line.split(";");
            String topicName = parts[0];
            int partitionsCount = Integer.parseInt(parts[1]);
            broker.addTopic(topicName, partitionsCount);
        }

        //Reading messages
        int messagesCount = Integer.parseInt(sc.nextLine());

        System.out.println("===ADDING MESSAGES TO TOPICS===");
        for (int i=0;i<messagesCount;i++) {
            String line = sc.nextLine();
            String [] parts = line.split(";");
            String topic = parts[0];
            LocalDateTime timestamp = LocalDateTime.parse(parts[1]);
            String message = parts[2];
            if (parts.length==4) {
                String key = parts[3];
                try {
                    broker.addMessage(topic, new Message(timestamp,message,key));
                } catch (PartitionDoesNotExistException | UnsupportedOperationException e) {
                    System.out.println(e.getMessage());
                }
            }
            else {
                Integer partition = Integer.parseInt(parts[3]);
                String key = parts[4];
                try {
                    broker.addMessage(topic, new Message(timestamp,message,partition,key));
                } catch (PartitionDoesNotExistException | UnsupportedOperationException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        System.out.println("===BROKER STATE AFTER ADDITION OF MESSAGES===");
        System.out.println(broker);

        System.out.println("===CHANGE OF TOPICS CONFIGURATION===");
        //topics changes
        int changesCount = Integer.parseInt(sc.nextLine());
        for (int i=0;i<changesCount;i++){
            String line = sc.nextLine();
            String [] parts = line.split(";");
            String topicName = parts[0];
            Integer partitions = Integer.parseInt(parts[1]);
            try {
                broker.changeTopicSettings(topicName, partitions);
            } catch (UnsupportedOperationException e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("===ADDING NEW MESSAGES TO TOPICS===");
        messagesCount = Integer.parseInt(sc.nextLine());
        for (int i=0;i<messagesCount;i++) {
            String line = sc.nextLine();
            String [] parts = line.split(";");
            String topic = parts[0];
            LocalDateTime timestamp = LocalDateTime.parse(parts[1]);
            String message = parts[2];
            if (parts.length==4) {
                String key = parts[3];
                try {
                    broker.addMessage(topic, new Message(timestamp,message,key));
                } catch (PartitionDoesNotExistException | UnsupportedOperationException e) {
                    System.out.println(e.getMessage());
                }
            }
            else {
                Integer partition = Integer.parseInt(parts[3]);
                String key = parts[4];
                try {
                    broker.addMessage(topic, new Message(timestamp,message,partition,key));
                } catch (PartitionDoesNotExistException | UnsupportedOperationException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        System.out.println("===BROKER STATE AFTER CONFIGURATION CHANGE===");
        System.out.println(broker);


    }
}


