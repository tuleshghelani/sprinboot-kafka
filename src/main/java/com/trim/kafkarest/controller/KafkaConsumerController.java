package com.trim.kafkarest.controllers;

import com.trim.kafkarest.services.ConfluentKafkaConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kafka")
public class KafkaConsumerController {

    @Autowired
    private ConfluentKafkaConsumerService consumerService;

    @GetMapping("/consume/all")
    public String consumeAll() {
        new Thread(() -> consumerService.consumeMessages()).start();
        return "Started consuming all messages (check logs for output)";
    }

    @GetMapping("/consume/last24h")
    public String consumeLast24Hours() {
        new Thread(() -> consumerService.consumeMessagesFromLast24Hours()).start();
        return "Started consuming messages from last 24 hours (check logs for output)";
    }
}
