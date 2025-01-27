package com.example.pillyohae.global.distributedLock;

import org.springframework.stereotype.Component;


public class KeyValueProcessor {
    public static String kv(String key, String value) {
        return ("Processing Key: " + key + ", Value: " + value);
    }
}