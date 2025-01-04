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
import java.util.Set;

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
        Map<String, Integer> roadmap,
        List<String> skills,
        Set<Practice> practice,
        List<SkillDescription> skillsDescriptions
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
            String answer,
            String topic,
            Boolean done
        ) {
            @Override
            public boolean equals(Object obj) {
                if (this == obj) return true;
                if (obj == null || getClass() != obj.getClass()) return false;
                Practice practice = (Practice) obj;
                return question != null && question.equals(practice.question);
            }

            @Override
            public int hashCode() {
                return question != null ? question.hashCode() : 0;
            }
        }

        @Builder
        public record SkillDescription(
            String name,
            String description,
            List<Link> documentationLinks,
            List<Link> practiceLinks
        ) {
            @Builder
            public record Link(
                String name,
                String url
            ) { }
        }
    }
}
