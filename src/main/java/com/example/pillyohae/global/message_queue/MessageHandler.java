package com.example.pillyohae.global.message_queue;

@FunctionalInterface
interface MessageHandler {
    void handle(Object data);
}