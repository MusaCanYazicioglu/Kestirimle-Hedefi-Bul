import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class Sensor2 {
    protected static double KerterizFinder(double lat1, double lon1, double lat2, double lon2) {
        if((lat1<lat2&&lon1<lon2)||(lat1>lat2&&lon1>lon2))
        {
            double y = lon2 - lon1;
            double x = lat2 - lat1;
            return ( Math.toDegrees(Math.atan2(y, x)) + 360 ) % 360;
        }
        else{
            double y = lon1 - lon2;
            double x = lat1 - lat2;
            return ( Math.toDegrees(Math.atan2(y, x)) + 360 ) % 360;
        }
    }

    public static void main(String[] args) {

        ////Kafka consumer için config ayarlandı.
        Random rand = new Random();
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", "test-sinyal2");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer kafkaConsumer = new KafkaConsumer(properties);
        List topics = new ArrayList();
        topics.add("targetlocation");
        kafkaConsumer.subscribe(topics);

        //Kafka producer için config ayarlandı.
        Properties config = new Properties();
        config.put("client.id","Target 1");
        config.put("bootstrap.servers", "localhost:9092");
        config.put("acks", "all");
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer kp = new KafkaProducer(config);

        Gson g = new Gson();
        Location sensor;
        int X;
        int Y;

        //Hedeften gelen kordinat bilgileri ile hedefin kertezini tespit ederek kendi lokasyon bilgileri ile sensorlocation topicine pushladı.
        try{
            while (true){
                ConsumerRecords<String, String> records = kafkaConsumer.poll(10);
                for (ConsumerRecord<String, String> record: records){
                    X = rand.nextInt(1001);
                    Y = rand.nextInt(1001);
                    System.out.println(String.format("Topic - %s, Partition - %d, Value: %s", record.topic(), record.partition(), record.value()));
                    Location target  = g.fromJson(record.value(),Location.class);
                    sensor = new Location(X,Y, target.getId());
                    double kerteriz = KerterizFinder(sensor.getX(), sensor.getY(), target.getX(), target.getY());

                    try{
                        kp.send(new ProducerRecord("sensorlocation", null, "{"+"x:"+ sensor.getX() +",y:"+ sensor.getY() +",id:"+target.getId()+",kerteriz:"+kerteriz+"}"));
                    }
                    catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            kafkaConsumer.close();
        }
    }
}
