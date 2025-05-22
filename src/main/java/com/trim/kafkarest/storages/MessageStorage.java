package com.trim.kafkarest.storages;

import com.trim.kafkarest.model.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class MessageStorage {

	private final List<Message> storage = new CopyOnWriteArrayList<>();

	public void put(Message message) {
		storage.add(message);
	}

	public List<Message> getMessages() {
		return Collections.unmodifiableList(storage);
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		storage.forEach(msg -> buffer.append(msg).append("<br/>"));
		return buffer.toString();
	}
	
	public void clear() {
		storage.clear();
	}

}
