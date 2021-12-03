package kafka.examples;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/9/3 14:24
 * @Since: 1.0
 * @Package: cn.chh
 */
public class ProducerTest {

    public static final String BOOTSTRAP_SERVER = "localhost:9092";

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties properties = new Properties();

        properties.setProperty("bootstrap.servers", BOOTSTRAP_SERVER);
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
        producer.send(new ProducerRecord<>("one-more-topic", "test", "test")).get();
        producer.flush();
        producer.close();
    }

    public static void sendTTL() {
        ProducerRecord<String, String> record1 = new ProducerRecord<>("", 0, System.currentTimeMillis(),
                null, "msg_ttl_1", new RecordHeaders().add(new RecordHeader("ttl", new byte[]{})));
        
    }
}
