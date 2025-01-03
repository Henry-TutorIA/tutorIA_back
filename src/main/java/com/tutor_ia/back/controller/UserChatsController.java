package com.tutor_ia.back.controller;

import com.tutor_ia.back.domain.dto.SkillsDto;
import com.tutor_ia.back.services.UserChatsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/user/chats")
@AllArgsConstructor
public class UserChatsController {

    private UserChatsService userChatsService;

    @PostMapping("/{theme}")
    public ResponseEntity<String> createTheme(@RequestHeader("token") String userId, @PathVariable String theme) {
        return new ResponseEntity<>(userChatsService.createTheme(userId, theme.toLowerCase(Locale.ROOT)), HttpStatus.OK);
    }

    @PostMapping("/{theme}/leveling")
    public ResponseEntity<List<String>> leveling(@RequestHeader("token") String userId, @PathVariable String theme, @RequestBody List<SkillsDto> skills) {
        return new ResponseEntity<>(userChatsService.leveling(userId, theme.toLowerCase(Locale.ROOT), skills), HttpStatus.OK);
    }
}
