package com.tutor_ia.back.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;

import lombok.Data;
import lombok.NoArgsConstructor;
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
    String username,
    String password,
    Map<String, Chat> chats
) {
    @Builder(toBuilder = true)
    public record Chat (
        List<Questions> questions,
        List<String> roadmap,
        List<String> skills,
        List<Practice> practice
    ) {
        @Builder(toBuilder = true)
        public record Questions(
            String question,
            String answer,
            LocalDate date
        ) {}

        @Builder(toBuilder = true)
        public record Practice(
            String question,
            String topic,
            Boolean done
        ) {}
    }
}



