package com.example.androidpoc.model;

import java.io.Serializable;

public class TodoItem implements Serializable {
    public final String text;
    public final long createdAtMillis;

    public TodoItem(String text, long createdAtMillis) {
        this.text = text;
        this.createdAtMillis = createdAtMillis;
    }
}
