package com.tehnicomsoft.androidtest.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aleksandar on 12.10.16..
 */

public class Message {
    String name;
    String message;
    long created;

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public Message(String name, String message, long created) {
        this.name = name;
        this.message = message;
        this.created = created;
    }

    public Message() {

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
