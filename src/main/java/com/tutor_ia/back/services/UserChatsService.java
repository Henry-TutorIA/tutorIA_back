package com.tutor_ia.back.services;

import com.tutor_ia.back.domain.ChatResponse;
import com.tutor_ia.back.domain.exceptions.InvalidValueException;
import com.tutor_ia.back.repository.IARepository;
import com.tutor_ia.back.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserChatsService {

    private final IARepository iaRepository;

    private final UserRepository userRepository;

    public Boolean checkTheme(String theme) {
        return Optional.ofNullable(iaRepository.checkTheme(theme))
                .map(ChatResponse::response)
                .filter(Boolean::booleanValue)
                .orElseThrow(() -> new InvalidValueException(theme));
    }
}
