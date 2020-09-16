package com.example.demo001.interceptor;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

public class KafkaInterceptor implements ProducerInterceptor {

    @Override
    public ProducerRecord onSend(ProducerRecord record) {
        Object value = record.value();
        return new ProducerRecord(record.topic(), record.partition(), record.timestamp(), record.key(), "test--" + value, record.headers());
    }

    @Override
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {

    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }

}
