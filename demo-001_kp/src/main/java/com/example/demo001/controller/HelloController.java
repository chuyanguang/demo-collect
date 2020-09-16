package com.example.demo001.controller;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hello")
public class HelloController {

    public static final String VAL_TOPIC = "demo";
    @Autowired(required = false)
    private KafkaTemplate<String, Object> kafkaTemplate;

    @GetMapping("send/{msg}")
    public String produceMessage(@PathVariable String msg){
        final Boolean result = kafkaTemplate.executeInTransaction(kafkaOperations -> {
            try {
                kafkaOperations.send(new ProducerRecord<>(VAL_TOPIC, "k001", msg));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        });
        return "执行："+result;
    }

}
