package com.tutor_ia.back.services;

import com.tutor_ia.back.domain.ChatResponse;
import com.tutor_ia.back.domain.User;
import com.tutor_ia.back.domain.exceptions.AlreadyExistsException;
import com.tutor_ia.back.domain.exceptions.InvalidValueException;
import com.tutor_ia.back.domain.exceptions.UserNotFoundException;
import com.tutor_ia.back.repository.IARepository;
import com.tutor_ia.back.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserChatsService {

    private final IARepository iaRepository;

    private final UserRepository userRepository;

    public String createTheme(String userId, String theme) {
        Optional.ofNullable(iaRepository.isValidTheme(theme))
                .map(ChatResponse::response)
                .filter(Boolean::booleanValue)
                .orElseThrow(() -> new InvalidValueException(theme));

        var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        createChat(user, theme);

        userRepository.save(user);

        return theme;
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
