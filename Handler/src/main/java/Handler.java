import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.util.*;
import redis.clients.jedis.*;

public class Handler {

    protected static void TargetFinder(double lat1, double lon1, double m1, double lat3, double lon3, double m3) {
        double m1s = Math.tan(Math.toRadians(m1));
        double m3s = Math.tan(Math.toRadians(m3));
        double b1 = ((-lat1) * m1s) + lon1;
        double b3 = ((-lat3) * m3s) + lon3;
        double x = (b3-b1)/(m1s-m3s);
        double y = (m1s*x)+b1;
        System.out.println("Hedef kordinatları -> X : " + Math.round(x) + "  Y : " + Math.round(y) );
    }

    public static void main(String[] args) {

        Random rand = new Random();

        //Kafka consumer için config ayarlandı.
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", "test-handler");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer kafkaConsumer = new KafkaConsumer(properties);
        List topics = new ArrayList();
        topics.add("sensorlocation");
        kafkaConsumer.subscribe(topics);

        Gson g = new Gson();

        //Redis bağlatısı ayarlandı.
        JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);

        //Sensorlerden gelen bilgiler idlere göre eşleştirilip hedef lokasyonun kartezyen kordinatları hesaplanıyor.
        try{
            while (true){
                ConsumerRecords<String, String> records = kafkaConsumer.poll(10);
                for (ConsumerRecord<String, String> record: records) {
                    System.out.println(String.format("Topic - %s, Partition - %d, Value: %s", record.topic(), record.partition(), record.value()));
                    Location sensor1 = g.fromJson(record.value(), Location.class);
                    try (Jedis jedis = jedisPool.getResource()) {
                        if (jedis.exists(sensor1.getId())) {
                            Location sensor2 = g.fromJson(jedis.get(sensor1.getId()), Location.class);
                            TargetFinder(sensor2.getX(),sensor2.getY(),sensor2.getKerteriz(),sensor1.getX(),sensor1.getY(),sensor1.getKerteriz());
                        } else {
                            jedis.set(sensor1.getId(), record.value());
                        }
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            kafkaConsumer.close();
            jedisPool.close();
        }
    }
}
