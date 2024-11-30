package com.tutor_ia.back.services;

import com.tutor_ia.back.domain.User;
import com.tutor_ia.back.domain.dto.TokenDto;
import com.tutor_ia.back.domain.dto.UserDto;
import com.tutor_ia.back.domain.exceptions.IncorrectPasswordException;
import com.tutor_ia.back.domain.exceptions.UserAlreadyExistsException;
import com.tutor_ia.back.domain.exceptions.UserNotFoundException;
import com.tutor_ia.back.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService {

    private UserRepository userRepository;

    public TokenDto register(UserDto userDto) {
        if (userRepository.existsById(userDto.email())) {
            throw new UserAlreadyExistsException();
        }

        User user = User.builder()
            .email(userDto.email())
            .userName(userDto.userName())
            .password(userDto.password())
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
                .token(null)
                .build();
    }
}
