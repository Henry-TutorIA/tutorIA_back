package com.tutor_ia.back.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutor_ia.back.domain.ChatResponse;
import com.tutor_ia.back.domain.User;
import com.tutor_ia.back.domain.dto.SkillsDto;
import com.tutor_ia.back.domain.exceptions.AlreadyExistsException;
import com.tutor_ia.back.domain.exceptions.InvalidValueException;
import com.tutor_ia.back.domain.exceptions.UserNotFoundException;
import com.tutor_ia.back.repository.IARepository;
import com.tutor_ia.back.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserChatsService {

    private final IARepository iaRepository;

    private final UserRepository userRepository;

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

    public List<String> leveling(String userId, String theme, List<SkillsDto> skills) {
        var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        var practices = Optional.ofNullable(iaRepository.leveling(user, theme, skills))
                .map(ChatResponse::response);


        return practices.map(list -> list.stream()
                        .map(User.Chat.Practice::question)
                        .toList())
                .orElse(List.of());
    }

    private void createChat(User user, String theme) {
        if (user.chats().containsKey(theme)) {
            throw new AlreadyExistsException("Theme");
        }

        var newChat = User.Chat.builder()
                .questions(Collections.emptyList())
                .roadmap(Collections.emptyList())
                .skills(Collections.emptyList())
                .practice(Collections.emptyList())
                .build();

        user.chats().put(theme, newChat);
    }
}
