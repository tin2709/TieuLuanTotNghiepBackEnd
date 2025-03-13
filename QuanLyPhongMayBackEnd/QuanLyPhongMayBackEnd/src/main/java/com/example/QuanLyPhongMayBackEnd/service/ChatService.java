//package com.example.QuanLyPhongMayBackEnd.service;
//
//import org.springframework.stereotype.Service;
//import org.springframework.ai.chat.client.ChatClient;
//
//@Service
//public class ChatService {
//
//    private final ChatClient chatClient;
//
//    public ChatService(ChatClient.Builder chatClientBuilder) {
//        this.chatClient = chatClientBuilder.build();
//    }
//
//    public String askToDeepSeekAI(String question) {
//        return chatClient.prompt(question)
//                .call().content();
//    }
//}
