package com.tutor_ia.back.domain.dto;

import com.tutor_ia.back.domain.User;
import lombok.Builder;

import java.util.*;
import java.util.stream.Collectors;

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
                .roadmap("")
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
                .roadmap(String.join(",", Optional.ofNullable(user.chats().get(theme)).map(User.Chat::roadmap).map(Map::keySet).orElse(Set.of())))
                .skills(Optional.ofNullable(user.chats().get(theme))
                        .map(User.Chat::skills)
                        .map(skillsMap -> skillsMap.stream()
                                .collect(Collectors.toMap(Object::toString, key -> "intermediate")))
                        .orElse(Map.of()))
                .question(question)
                .history(Map.of())
                .build();
    }
}
