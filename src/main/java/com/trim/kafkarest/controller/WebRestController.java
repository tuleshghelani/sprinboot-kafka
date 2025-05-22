package com.trim.kafkarest.controller;

import com.trim.kafkarest.dto.MessageDTO;
import com.trim.kafkarest.model.Message;
import com.trim.kafkarest.services.KafkaProducer;
import com.trim.kafkarest.services.KafkaReaderService;
import com.trim.kafkarest.storages.MessageStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "trim/kafka")
public class WebRestController {

	private static final Logger LOG = LoggerFactory
			.getLogger(WebRestController.class);

	@Autowired
	private KafkaProducer kafkaProducer;
	
	@Autowired
	private KafkaReaderService kafkaReaderService;

	@Autowired
	private MessageStorage storage;

	@GetMapping(value = "/producer")
	public String producer(@RequestParam("data") String data) {
		kafkaProducer.send(data);
		LOG.info("data value : {}", data);
		LOG.info("storage value : {}", storage.toString());
		return "Done";
	}

	@PostMapping(value = "/publish")
	public ResponseEntity<String> publish(@RequestBody MessageDTO messageDTO) {
		LOG.info("Publishing message: {}", messageDTO.getContent());
		kafkaProducer.send(messageDTO.getContent());
		return new ResponseEntity<>("Message sent to Kafka topic", HttpStatus.OK);
	}

	@GetMapping(value = "/consumer")
	public String consumer() {
		// Read directly from Kafka instead of storage
		List<Message> messages = kafkaReaderService.readMessages();
		StringBuilder sb = new StringBuilder();
		messages.forEach(msg -> sb.append(msg).append("<br/>"));
		return sb.toString();
	}

	@GetMapping(value = "/messages")
	public ResponseEntity<List<Message>> getAllMessages() {
		// Read directly from Kafka instead of storage
		return new ResponseEntity<>(kafkaReaderService.readMessages(), HttpStatus.OK);
	}

	@DeleteMapping(value = "/messages")
	public ResponseEntity<String> clearMessages() {
		storage.clear();
		return new ResponseEntity<>("All messages cleared", HttpStatus.OK);
	}

}
