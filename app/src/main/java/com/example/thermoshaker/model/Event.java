package com.example.thermoshaker.model;

public class Event {
    int code;
    private String Type;
    private String content;
    public Event(String type) {
        Type = type;
    }

    public Event(String type, String content) {
        Type = type;
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    @Override
    public String toString() {
        return "Event{" +
                "code=" + code +
                ", Type='" + Type + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
