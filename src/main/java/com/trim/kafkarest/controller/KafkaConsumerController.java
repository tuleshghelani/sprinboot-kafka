package com.trim.kafkarest.controllers;

import com.trim.kafkarest.services.ConfluentKafkaConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/kafka")
public class KafkaConsumerController {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumerController.class);

    @Autowired
    private ConfluentKafkaConsumerService consumerService;

    @GetMapping("/consume/all")
    public String consumeAll() {
        new Thread(() -> consumerService.consumeMessages()).start();
        return "Started consuming all messages (check logs for output)";
    }

    @GetMapping("/consume/last24h")
    public String consumeLast24Hours() {
        // Run in background thread and return immediately
        new Thread(() -> {
            try {
                consumerService.consumeMessagesFromLast24Hours();
            } catch (Exception e) {
                LOG.error("Error consuming messages from last 24 hours", e);
            }
        }).start();
        return "Started consuming messages from last 24 hours (check logs for output)";
    }

    // Alternative endpoint that returns the messages
    @GetMapping("/consume/last24h/list")
    public ResponseEntity<List<String>> consumeLast24HoursAsList() {
        List<String> messages = consumerService.consumeMessagesFromLast24HoursAsList();
        return ResponseEntity.ok(messages);
    }
}
