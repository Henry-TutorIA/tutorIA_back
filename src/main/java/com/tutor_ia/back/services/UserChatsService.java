package com.tutor_ia.back.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutor_ia.back.domain.ChatResponse;
import com.tutor_ia.back.domain.User;
import com.tutor_ia.back.domain.dto.RoadMapDto;
import com.tutor_ia.back.domain.dto.ScoreDto;
import com.tutor_ia.back.domain.dto.SkillsDto;
import com.tutor_ia.back.domain.exceptions.AlreadyExistsException;
import com.tutor_ia.back.domain.exceptions.InvalidValueException;
import com.tutor_ia.back.domain.exceptions.UserNotFoundException;
import com.tutor_ia.back.repository.IARepository;
import com.tutor_ia.back.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserChatsService {

    private final IARepository iaRepository;

    private final UserRepository userRepository;

    public Set<String> getAllThemes(String userId) {
        return userRepository.findById(userId)
                .map(User::chats)
                .map(Map::keySet)
                .orElseThrow(UserNotFoundException::new);
    }

    public String createTheme(String userId, String theme) {
        var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Optional.ofNullable(iaRepository.isValidTheme(user, theme))
                .map(ChatResponse::response)
                .map(Boolean::valueOf)
                .filter(Boolean::booleanValue)
                .orElseThrow(() -> new InvalidValueException(theme));


        createChat(user, theme);
        userRepository.save(user);

        return theme;
    }

    public Set<User.Chat.Practice> leveling(String userId, String theme, List<SkillsDto> skills) {
        var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        var topics = skills.stream().map(SkillsDto::name).toList();
        var questions = user.chats().get(theme).practice()
                .stream().filter(practice -> topics.contains(practice.topic())).collect(Collectors.toSet());

        if (!questions.isEmpty()) {
            return questions;
        }

        questions = Optional.ofNullable(iaRepository.leveling(user, theme, skills))
                .map(ChatResponse::response)
                .orElseThrow(() -> new InvalidValueException("questions"));

        user.chats().get(theme).practice().addAll(questions);
        userRepository.save(user);

        return questions;
    }

    public ScoreDto evaluate(String userId, String theme, List<User.Chat.Practice> exercises) {
        var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        var response = Optional.ofNullable(iaRepository.evaluate(user, theme, exercises))
                .map(ChatResponse::response)
                .orElseThrow(() -> new InvalidValueException("exercises"));



        var roadMap = user.chats().get(theme).roadmap();
        var skills = user.chats().get(theme).skills();
        var skillsDescriptions = user.chats().get(theme).skillsDescriptions();

        var practiceUpdated = user.chats().get(theme).practice().stream()
                .map(practice -> {
                    if (response.doneExercises().contains(practice.question()) && !practice.done()) {
                        var topic = practice.topic().toLowerCase(Locale.ROOT);
                        var quantity = roadMap.getOrDefault(practice.topic(), 0) + 1;

                        if (quantity >= 2) {
                            response.skills().add(topic);
                            skills.add(topic);
                            roadMap.remove(topic);
                            skillsDescriptions.removeIf(description -> description.name().equalsIgnoreCase(topic));
                        } else {
                            roadMap.put(topic, quantity);
                        }

                        return practice.toBuilder().done(true).build();
                    }
                    return practice;
                })
                .collect(Collectors.toSet());

        if (roadMap.keySet().size() <= 10) {
            response.roadmap().forEach(topic -> {
                roadMap.put(topic.toLowerCase(Locale.ROOT), 0);
            });
        }

        user.chats().put(theme, user.chats().get(theme).toBuilder()
                        .roadmap(roadMap)
                        .skills(skills)
                        .practice(practiceUpdated)
                        .skillsDescriptions(skillsDescriptions)
                .build());
        userRepository.save(user);

        return response;
    }

    public List<RoadMapDto> getRoadMap(String userId, String theme) {
        var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        var roadMap = Optional.ofNullable(user.chats().get(theme))
                .map(User.Chat::roadmap)
                .filter(map -> !map.isEmpty())
                .orElseGet(() -> Optional.ofNullable(iaRepository.getNewRoadMap(user, theme))
                        .map(response -> {
                            if (!user.chats().containsKey(theme)) {
                                createChat(user, theme);
                            }

                            var roadMapResponse = response.response();
                            var lowerCaseMap = roadMapResponse.entrySet()
                                    .stream()
                                    .collect(Collectors.toMap(
                                            entry -> entry.getKey().toLowerCase(), // Transforma la key a minúsculas
                                            Map.Entry::getValue // Conserva el valor
                                    ));

                            user.chats().put(theme.toLowerCase(Locale.ROOT), user.chats().get(theme).toBuilder().roadmap(lowerCaseMap).build());
                            userRepository.save(user);

                            return response.response();
                        })
                        .orElse(Map.of()));

        var roadMapList = new ArrayList<RoadMapDto>();

        roadMap.forEach((topic, progress) -> {
            roadMapList.add(RoadMapDto
                    .builder()
                    .name(topic.toLowerCase(Locale.ROOT))
                    .progress(progress)
                    .build());
        });

        return roadMapList;
    }

    public User.Chat.SkillDescription getSkillDescription(String userId, String theme, String skill) {
        var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        var chat = user.chats().get(theme);
        if (chat.roadmap().keySet().stream().noneMatch(roadmap -> roadmap.equalsIgnoreCase(skill))) {
            throw new InvalidValueException("skill");
        }

        return chat.skillsDescriptions().stream()
                .filter(skillDescription -> skillDescription.name().equalsIgnoreCase(skill))
                .findFirst().orElseGet(() -> {
                    var description = iaRepository.getNewSkillDescription(user, theme, skill).response();
                    chat.skillsDescriptions().add(description);
                    user.chats().put(theme.toLowerCase(Locale.ROOT), chat.toBuilder().skillsDescriptions(chat.skillsDescriptions()).build());

                    userRepository.save(user);

                    return description;
                });
    }

    private void createChat(User user, String theme) {
        if (user.chats().containsKey(theme)) {
            throw new AlreadyExistsException("Theme");
        }

        var newChat = User.Chat.builder()
                .questions(Collections.emptyList())
                .roadmap(Collections.emptyMap())
                .skills(Collections.emptyList())
                .practice(Collections.emptySet())
                .skillsDescriptions(Collections.emptyList())
                .build();

        user.chats().put(theme, newChat);
    }
}
