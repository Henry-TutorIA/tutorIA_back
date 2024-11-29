package com.tutor_ia.back.domain;

import lombok.Builder;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Document(collection = "user")
@Builder(toBuilder = true)
public record User(
    @Id
    String email,
    String userName,
    String password,
    Chats chats
) {
    public record Chats (
            Map<String, Chat> chat
    ) {
        public record Chat (
                List<Questions> questions,
                List<String> roadmap,
                List<String> skills,
                List<Practice> practice
        ) {
            public record Questions(
                    String question,
                    String answer,
                    LocalDate date
            ) {}

            public record Practice(
                    String instruction,
                    List<String> topic
            ) {}
        }
    }
}


