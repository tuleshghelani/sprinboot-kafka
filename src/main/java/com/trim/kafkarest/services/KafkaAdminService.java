package com.trim.kafkarest.services;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaAdminService {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaAdminService.class);

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.topic}")
    private String topic;

    public void deleteAllMessages() {
        LOG.info("Deleting all messages from topic: {}", topic);
        try (AdminClient adminClient = createAdminClient()) {
            // Delete the topic
            adminClient.deleteTopics(Collections.singletonList(topic)).all().get();
            LOG.info("Topic {} deleted successfully", topic);
            
            Thread.sleep(1000);
            
            // Recreate the topic with the same settings
            NewTopic newTopic = new NewTopic(topic, 1, (short) 1);
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
            LOG.info("Topic {} recreated successfully", topic);
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("Error clearing messages from topic {}: {}", topic, e.getMessage(), e);
            throw new RuntimeException("Failed to clear messages from Kafka", e);
        }
    }
    
    private AdminClient createAdminClient() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return AdminClient.create(props);
    }
}