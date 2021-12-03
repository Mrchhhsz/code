package kafka.examples;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/11/26 23:12
 * @Since: 1.0
 * @Package: kafka.examples
 */
public class ConsumerTest {

    public final static String GROUP_ID = "kafka-clients-group";

    public static void main(String[] args) {
        Properties prop = new Properties();
        prop.setProperty("bootstrap.servers", ProducerTest.BOOTSTRAP_SERVER);
        prop.setProperty("group.id", GROUP_ID);
        prop.setProperty("key.deserializer", StringDeserializer.class.getName());
        prop.setProperty("value.deserializer", StringDeserializer.class.getName());
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(prop);
        consumer.subscribe(Arrays.asList("one-more-topic"));

        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(20000));
        for(ConsumerRecord<String, String> record : records) {
            System.out.println(record.key() + ":" + record.value());
        }
    }
}
