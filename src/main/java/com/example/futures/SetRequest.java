package com.example.futures;

public class SetRequest {
    private final String key;
    private final Object value;

    public SetRequest(final String key, final Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
