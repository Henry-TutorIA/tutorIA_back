package com.tutor_ia.back.services;

import com.tutor_ia.back.domain.User;
import com.tutor_ia.back.domain.dto.TokenDto;
import com.tutor_ia.back.domain.dto.UserDto;
import com.tutor_ia.back.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService {

    private UserRepository userRepository;

    public TokenDto register(UserDto userDto) throws Exception {
        if (userRepository.existsById(userDto.email())) {
            throw new Exception("Email already exists");
        }

        User user = User.builder()
            .email(userDto.email())
            .userName(userDto.userName())
            .password(userDto.password())
            .build();

        userRepository.save(user);

        return getToken(user);
    }

    public TokenDto login(UserDto userDto) throws Exception {
        User userFound = userRepository.findById(userDto.email())
                .orElseThrow(() -> new Exception("The email doesn't exist"));

        if (!isSamePassword(userFound, userDto)) throw new Exception("Incorrect Password");

        return getToken(userFound);
    }

    private boolean isSamePassword(User registeredUser, UserDto loginUser) throws Exception {
        return registeredUser.password().equals(loginUser.password());
    }

    private TokenDto getToken(User user) {
        return TokenDto.builder()
                .token(null)
                .build();
    }
}
