package com.tutor_ia.back.controller;

import com.tutor_ia.back.services.UserChatsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/chats")
@AllArgsConstructor
public class UserChatsController {

    private UserChatsService userChatsService;

    @GetMapping("/check-theme/{theme}")
    public ResponseEntity<Boolean> checkTheme(@PathVariable String theme) {
        return new ResponseEntity<>(userChatsService.checkTheme(theme), HttpStatus.OK);
    }
}
