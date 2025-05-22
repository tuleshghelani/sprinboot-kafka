package com.trim.kafkarest.services;

import com.trim.kafkarest.model.Message;
import com.trim.kafkarest.storages.MessageStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

	private static final Logger LOG = LoggerFactory
			.getLogger(KafkaConsumer.class);

	@Autowired
	private MessageStorage messageStorage;

	@KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
	public void processMessage(String content) {
		LOG.info("Received message: {}", content);
		Message message = new Message(content);
		messageStorage.put(message);
		LOG.info("Message processed and stored: {}", message);
	}
}
