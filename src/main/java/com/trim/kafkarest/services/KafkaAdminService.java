package com.trim.kafkarest.services;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaAdminService {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaAdminService.class);

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.topic}")
    private String topic;

    /*public void deleteAllMessages() {
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
    }*/

    
    public void deleteAllMessages() {
        LOG.info("Deleting all messages from topic: {}", topic);
        try (AdminClient adminClient = createAdminClient()) {
            // Get topic info to find partition count
            DescribeTopicsResult topicsResult = adminClient.describeTopics(Collections.singletonList(topic));
            TopicDescription topicDescription = topicsResult.values().get(topic).get();
            
            // For each partition, create a TopicPartition and set offset to the end
            Map<TopicPartition, RecordsToDelete> recordsToDelete = new HashMap<>();
            topicDescription.partitions().forEach(partitionInfo -> {
                TopicPartition topicPartition = new TopicPartition(topic, partitionInfo.partition());
                // Delete all records up to the end offset (which removes all messages)
                recordsToDelete.put(topicPartition, RecordsToDelete.beforeOffset(Long.MAX_VALUE));
            });
            
            // Delete the records
            DeleteRecordsResult result = adminClient.deleteRecords(recordsToDelete);
            result.all().get();
            
            LOG.info("Successfully deleted all messages from topic {}", topic);
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("Error deleting messages from topic {}: {}", topic, e.getMessage(), e);
            throw new RuntimeException("Failed to delete messages from Kafka", e);
        }
    }
    
    private AdminClient createAdminClient() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return AdminClient.create(props);
    }
}