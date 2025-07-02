package com.trim.kafkarest.services;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.ArrayList;

@Service
public class ConfluentKafkaConsumerService {
    private static final Logger LOG = LoggerFactory.getLogger(ConfluentKafkaConsumerService.class);

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.topic}")
    private String topic;

    @Value("${spring.kafka.properties.sasl.jaas.config}")
    private String jaasConfig;

    @Value("${spring.kafka.properties.sasl.mechanism}")
    private String saslMechanism;

    @Value("${spring.kafka.properties.security.protocol}")
    private String securityProtocol;

    public void consumeMessages() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", "your-group-id");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("sasl.jaas.config", jaasConfig);
        props.put("sasl.mechanism", saslMechanism);
        props.put("security.protocol", securityProtocol);
        props.put("auto.offset.reset", "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(topic));
            LOG.info("Subscribed to topic: {}", topic);

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    LOG.info("Consumed message: key={}, value={}, offset={}", record.key(), record.value(), record.offset());
                }
            }
        }
    }

    public void consumeMessagesFromLast24Hours() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", "your-group-id");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("sasl.jaas.config", jaasConfig);
        props.put("sasl.mechanism", saslMechanism);
        props.put("security.protocol", securityProtocol);
        props.put("auto.offset.reset", "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(topic));
            LOG.info("Subscribed to topic: {}", topic);

            // Wait for partition assignment
            consumer.poll(Duration.ofMillis(1000));
            Set<TopicPartition> partitions = consumer.assignment();
            while (partitions.isEmpty()) {
                consumer.poll(Duration.ofMillis(100));
                partitions = consumer.assignment();
            }

            // Calculate timestamp for 24 hours ago
            long now = System.currentTimeMillis();
            long timestamp24hAgo = now - 24 * 60 * 60 * 1000;

            // Prepare timestamp map for each partition
            Map<TopicPartition, Long> timestampsToSearch = new HashMap<>();
            for (TopicPartition partition : partitions) {
                timestampsToSearch.put(partition, timestamp24hAgo);
            }

            // Fetch offsets for the timestamp
            Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes = consumer.offsetsForTimes(timestampsToSearch);

            // Seek to the offset for each partition
            for (TopicPartition partition : partitions) {
                OffsetAndTimestamp offsetAndTimestamp = offsetsForTimes.get(partition);
                if (offsetAndTimestamp != null) {
                    consumer.seek(partition, offsetAndTimestamp.offset());
                } else {
                    // If no offset found for timestamp, seek to beginning
                    consumer.seekToBeginning(Collections.singletonList(partition));
                }
            }

            // Consume messages with a timeout or limit
            int messageCount = 0;
            int maxMessages = 1000; // Limit the number of messages to consume
            long startTime = System.currentTimeMillis();
            long timeoutMs = 30000; // 30 seconds timeout

            while (messageCount < maxMessages && (System.currentTimeMillis() - startTime) < timeoutMs) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                
                if (records.isEmpty()) {
                    // No more messages available, break out
                    break;
                }
                
                for (ConsumerRecord<String, String> record : records) {
                    LOG.info("Consumed message: key={}, value={}, offset={}, timestamp={}", 
                            record.key(), record.value(), record.offset(), record.timestamp());
                    messageCount++;
                    
                    if (messageCount >= maxMessages) {
                        break;
                    }
                }
            }
            
            LOG.info("Finished consuming messages. Total consumed: {}", messageCount);
        }
    }

    // Alternative method that returns a list of messages instead of just logging
    public List<String> consumeMessagesFromLast24HoursAsList() {
        List<String> messages = new ArrayList<>();
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", "your-group-id");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("sasl.jaas.config", jaasConfig);
        props.put("sasl.mechanism", saslMechanism);
        props.put("security.protocol", securityProtocol);
        props.put("auto.offset.reset", "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(topic));
            LOG.info("Subscribed to topic: {}", topic);

            // Wait for partition assignment
            consumer.poll(Duration.ofMillis(1000));
            Set<TopicPartition> partitions = consumer.assignment();
            while (partitions.isEmpty()) {
                consumer.poll(Duration.ofMillis(100));
                partitions = consumer.assignment();
            }

            // Calculate timestamp for 24 hours ago
            long now = System.currentTimeMillis();
            long timestamp24hAgo = now - 24 * 60 * 60 * 1000;

            // Prepare timestamp map for each partition
            Map<TopicPartition, Long> timestampsToSearch = new HashMap<>();
            for (TopicPartition partition : partitions) {
                timestampsToSearch.put(partition, timestamp24hAgo);
            }

            // Fetch offsets for the timestamp
            Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes = consumer.offsetsForTimes(timestampsToSearch);

            // Seek to the offset for each partition
            for (TopicPartition partition : partitions) {
                OffsetAndTimestamp offsetAndTimestamp = offsetsForTimes.get(partition);
                if (offsetAndTimestamp != null) {
                    consumer.seek(partition, offsetAndTimestamp.offset());
                } else {
                    // If no offset found for timestamp, seek to beginning
                    consumer.seekToBeginning(Collections.singletonList(partition));
                }
            }

            // Consume messages and collect them
            int messageCount = 0;
            int maxMessages = 1000;
            long startTime = System.currentTimeMillis();
            long timeoutMs = 30000;

            while (messageCount < maxMessages && (System.currentTimeMillis() - startTime) < timeoutMs) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                
                if (records.isEmpty()) {
                    break;
                }
                
                for (ConsumerRecord<String, String> record : records) {
                    messages.add(String.format("key=%s, value=%s, offset=%d, timestamp=%d", 
                            record.key(), record.value(), record.offset(), record.timestamp()));
                    messageCount++;
                    
                    if (messageCount >= maxMessages) {
                        break;
                    }
                }
            }
            
            LOG.info("Finished consuming messages. Total consumed: {}", messageCount);
        }
        
        return messages;
    }
}