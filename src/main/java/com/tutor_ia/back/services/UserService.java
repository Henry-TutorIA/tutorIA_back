package com.tutor_ia.back.services;

import com.tutor_ia.back.domain.User;
import com.tutor_ia.back.domain.dto.TokenDto;
import com.tutor_ia.back.domain.dto.UserDto;
import com.tutor_ia.back.domain.exceptions.IncorrectPasswordException;
import com.tutor_ia.back.domain.exceptions.AlreadyExistsException;
import com.tutor_ia.back.domain.exceptions.UserNotFoundException;
import com.tutor_ia.back.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    public TokenDto register(UserDto userDto) {
        if (userRepository.existsById(userDto.email())) {
            throw new AlreadyExistsException("user");
        }

        User user = User.builder()
            .email(userDto.email())
            .username(userDto.username())
            .password(userDto.password())
            .chats(new HashMap<>())
            .build();

        userRepository.save(user);

        return getToken(user);
    }

    public TokenDto login(UserDto userDto) {
        User userFound = userRepository.findById(userDto.email())
                .orElseThrow(UserNotFoundException::new);

        if (!isSamePassword(userFound, userDto)) throw new IncorrectPasswordException();

        return getToken(userFound);
    }

    private boolean isSamePassword(User registeredUser, UserDto loginUser) {
        return registeredUser.password().equals(loginUser.password());
    }

    private TokenDto getToken(User user) {
        return TokenDto.builder()
                .token(user.email())
                .username(user.username())
                .build();
    }
}
