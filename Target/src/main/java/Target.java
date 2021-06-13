import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Target {

    public static void main(String[] args) {

        Random rand = new Random();

        //Kafka producer için config ayarlandı.
        Properties config = new Properties();
        config.put("client.id","Target 1");
        config.put("bootstrap.servers", "localhost:9092");
        config.put("acks", "all");
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer kp = new KafkaProducer(config);

        Location target;

        while(true){

            int X = rand.nextInt(1001);
            int Y = rand.nextInt(1001);
            Long id = System.currentTimeMillis();

            target =  new Location(X,Y,id);

            //Kafka targetlocation topicine üretilen kordinat pushlandı.
            try{
                System.out.println("{"+"x:"+ target.getX() +",y:"+ target.getY() +",id:"+target.getId()+"}");
                kp.send(new ProducerRecord("targetlocation", null, "{"+"x:"+ target.getX() +",y:"+ target.getY() +",id:"+target.getId()+"}"));
                TimeUnit.SECONDS.sleep(10000);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
}
