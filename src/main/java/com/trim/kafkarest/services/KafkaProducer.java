package com.trim.kafkarest.services;

import com.trim.kafkarest.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducer {
	
	private static final Logger LOG = LoggerFactory.getLogger(KafkaProducer.class);
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	@Value("${app.kafka.topic}")
	private String topic;
	
	public void send(String data) {
		try {
			LOG.info("Sending data: {}", data);
			
			CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, data);
			
			future.whenComplete((result, ex) -> {
				if (ex == null) {
					LOG.info("Successfully sent message: [{}] with offset: [{}]", 
							data, result.getRecordMetadata().offset());
				} else {
					LOG.error("Failed to send message: [{}]", data, ex);
				}
			});
		} catch (Exception e) {
			LOG.error("Failed to send message to Kafka: {}", e.getMessage());
		}
	}

}
