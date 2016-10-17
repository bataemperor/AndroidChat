package com.tehnicomsoft.androidtest.model;

/**
 * Created by aleksandar on 12.10.16..
 */

public class Message {
    private String name;
    private String message;
    private long created;
    private String imageUri;


    public Message(String name, String message, long created, String imageUri) {
        this.name = name;
        this.message = message;
        this.created = created;
        this.imageUri = imageUri;
    }

    public Message() {

    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }


    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
