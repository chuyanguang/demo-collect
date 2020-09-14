package com.example.demokafka;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

@Slf4j
@SpringBootTest
class DemoKafkaApplicationTests {

    private static final String SERVER_ADDR = "172.16.250.138:9092";
    private static final String VAL_TOPIC = "demo";
    private static Properties properties;

    @BeforeAll
    static void init(){
        properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, SERVER_ADDR);
    }

    @Test
    void producer() {
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        try {
            producer.send(new ProducerRecord<>(VAL_TOPIC, "k1", "hello"));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送消息失败");
        }
        producer.close();
    }

    @Test
    void test01() {
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "abc");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        while (true){
            consumer.subscribe(Collections.singletonList(VAL_TOPIC));
            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMinutes(1));
            for (ConsumerRecord<String, String> record : consumerRecords) {
                log.info("{}---{}", record.key(), record.value());
            }
        }
    }



}
