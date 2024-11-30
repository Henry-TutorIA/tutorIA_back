package com.tutor_ia.back.controller;

import com.tutor_ia.back.domain.dto.TokenDto;
import com.tutor_ia.back.domain.dto.UserDto;
import com.tutor_ia.back.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<TokenDto> register(@RequestBody @Valid UserDto user) throws Exception {
        return new ResponseEntity<>(userService.register(user), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody UserDto user) throws Exception {
        return new ResponseEntity<>(userService.login(user), HttpStatus.OK);
    }

}
