package com.trim.kafkarest.dto;

public class MessageDTO {
    private String content;
    
    public MessageDTO() {
    }
    
    public MessageDTO(String content) {
        this.content = content;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
} 