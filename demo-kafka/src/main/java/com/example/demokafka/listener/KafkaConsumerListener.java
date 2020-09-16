package com.example.demokafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Slf4j
@Component
public class KafkaConsumerListener {

    @KafkaListener(topics = "demo")
    void consumerMsg(ConsumerRecord<String, Object> record, Consumer consumer){
        log.info("{}--{}",record.key(), record.value());
        // 同步提交
//        consumer.commitSync();
        // 异步提交
        consumer.commitAsync((map, e) -> {
            if (StringUtils.isEmpty(e)) {
                log.info("提交offset成功");
                for (Map.Entry<TopicPartition, OffsetAndMetadata> entry : map.entrySet()) {
                    log.info("{}--{}", entry.getKey().partition(), entry.getValue().offset());
                }
            } else {
                log.error("提交offset失败");
            }
        });
    }

}
