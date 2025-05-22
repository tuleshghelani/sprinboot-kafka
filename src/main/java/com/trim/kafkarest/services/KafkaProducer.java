package com.trim.kafkarest.services;

import com.trim.kafkarest.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class KafkaProducer {
	
	private static final Logger LOG = LoggerFactory.getLogger(KafkaProducer.class);
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	@Value("${app.kafka.topic}")
	private String topic;
	
	public void send(String data) {
		LOG.info("Sending data: {}", data);
		
		ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, data);
		
		future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
			@Override
			public void onSuccess(SendResult<String, String> result) {
				LOG.info("Successfully sent message: [{}] with offset: [{}]", 
						data, result.getRecordMetadata().offset());
			}

			@Override
			public void onFailure(Throwable ex) {
				LOG.error("Failed to send message: [{}]", data, ex);
			}
		});
	}

}
