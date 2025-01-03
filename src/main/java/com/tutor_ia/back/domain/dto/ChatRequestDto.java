package com.tutor_ia.back.domain.dto;

import com.tutor_ia.back.domain.User;
import lombok.Builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Builder
public record ChatRequestDto (
    String username,
    String roadmap,
    Map<String, String> skills,
    String question,
    Map<String, Object> history
) {
    public static ChatRequestDto mapToChatRequestDtoWithSkills(User user, String theme, String question, List<SkillsDto> skills) {
        return ChatRequestDto.builder()
                .username(user.username())
                .roadmap(String.join(",", Optional.ofNullable(user.chats().get(theme)).map(User.Chat::roadmap).orElse(List.of())))
                .skills(getSkillsMap(skills))
                .question(question)
                .history(Map.of())
                .build();
    }

    private static Map<String, String> getSkillsMap(List<SkillsDto> skills) {
        var map = new HashMap<String, String>();

        for (SkillsDto skill : skills) {
            map.put(skill.name(), skill.level());
        }

        return map;
    }

    public static ChatRequestDto mapToChatRequestDto(User user, String theme, String question) {
        return ChatRequestDto.builder()
                .username(user.username())
                .roadmap(String.join(",", Optional.ofNullable(user.chats().get(theme)).map(User.Chat::roadmap).orElse(List.of())))
                .skills(Map.of())
                .question(question)
                .history(Map.of())
                .build();
    }
}
