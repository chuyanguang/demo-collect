package com.example.demokafka;

import com.example.demokafka.interceptor.KafkaInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

@Slf4j
@SpringBootTest
class DemoKafkaApplicationTests {

    private static final String SERVER_ADDR = "172.16.250.141:9092";
    private static final String VAL_TOPIC = "demo";
    private static Properties properties;

    @BeforeAll
    static void init() {
        properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, SERVER_ADDR);
    }

    @Test
    void producerTest01() {
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // 生产者拦截器
        properties.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, Collections.singletonList(KafkaInterceptor.class));
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        try {
            producer.send(new ProducerRecord<>(VAL_TOPIC, "k1", "hello"), (recordMetadata, e) -> {
                if (e == null) {
                    log.info("发送成功");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送消息失败");
        }
        producer.close();
    }

    /**
     * 带事务的生产者
     */
    @Test
    void producerTest02() {
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // 生产者拦截器
        properties.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, Collections.singletonList(KafkaInterceptor.class));
        // 开启幂等性
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, Boolean.TRUE);
        // 配置transactionID
        properties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "test_transaction");
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        try {
            // 初始化事务
            producer.initTransactions();
            // 开启事务
            producer.beginTransaction();
            producer.send(new ProducerRecord<>(VAL_TOPIC, "k1", "hello")/*, (recordMetadata, e) -> {
                if (e == null) {
                    log.info("发送成功");
                } else {
                    e.printStackTrace();
                    log.error("发送消息失败");
                    // 回滚事务
                    producer.abortTransaction();
                }
            }*/);
            // 提交事务
            producer.commitTransaction();
            log.info("发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送消息失败");
            // 回滚事务
            producer.abortTransaction();
        }
        producer.close();
    }

    @Test
    void consumerTest() {
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "demo-kafka01");
        // 关闭自动提交offset
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        while (true) {
            consumer.subscribe(Collections.singletonList(VAL_TOPIC));
//            consumer.assign(Collections.singletonList(new TopicPartition(VAL_TOPIC, 0)));     消费指定分区数据
//            consumer.seek(new TopicPartition(VAL_TOPIC, 0), 0);   // 从指定分区的自定义offset开始消费
            // 1分钟拉取一次
            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMinutes(1));

            for (ConsumerRecord<String, String> record : consumerRecords) {
                log.info("{}---{}", record.key(), record.value());
            }
            // 同步提交
//            consumer.commitSync();
            // 异步提交
//            consumer.commitAsync((map, e) -> {
//                if (StringUtils.isEmpty(e)) {
//                    log.info("提交offset成功");
//                    for (Map.Entry<TopicPartition, OffsetAndMetadata> entry : map.entrySet()) {
//                        log.info("{}--{}", entry.getKey().partition(), entry.getValue().offset());
//                    }
//                } else {
//                    log.error("提交offset失败");
//                }
//            });

        }
    }

    @Test
    void test02() {


    }


}
