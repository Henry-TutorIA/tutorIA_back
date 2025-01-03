package com.tutor_ia.back.controller;

import com.tutor_ia.back.domain.User;
import com.tutor_ia.back.domain.dto.RoadMapDto;
import com.tutor_ia.back.domain.dto.ScoreDto;
import com.tutor_ia.back.domain.dto.SkillsDto;
import com.tutor_ia.back.services.UserChatsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/user/chats")
@AllArgsConstructor
public class UserChatsController {

    private UserChatsService userChatsService;

    @GetMapping("/themes")
    public ResponseEntity<Set<String>> getThemes(@RequestHeader("token") String userId) {
        return new ResponseEntity<>(userChatsService.getAllThemes(userId), HttpStatus.OK);
    }

    @PostMapping("/{theme}")
    public ResponseEntity createTheme(@RequestHeader("token") String userId, @PathVariable String theme) {
        userChatsService.createTheme(userId, theme.toLowerCase(Locale.ROOT));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{theme}/leveling")
    public ResponseEntity<Set<User.Chat.Practice>> leveling(@RequestHeader("token") String userId, @PathVariable String theme, @RequestBody List<SkillsDto> skills) {
        return new ResponseEntity<>(userChatsService.leveling(userId, theme.toLowerCase(Locale.ROOT), skills), HttpStatus.OK);
    }

    @PostMapping("/{theme}/evaluate")
    public ResponseEntity<ScoreDto> evaluate(@RequestHeader("token") String userId, @PathVariable String theme, @RequestBody List<User.Chat.Practice> exercises) {
        return new ResponseEntity<>(userChatsService.evaluate(userId, theme, exercises), HttpStatus.OK);
    }

    @GetMapping("/{theme}/roadmap")
    public ResponseEntity<List<RoadMapDto>> getRoadMap(@RequestHeader("token") String userId, @PathVariable String theme) {
        return new ResponseEntity<>(userChatsService.getRoadMap(userId, theme), HttpStatus.OK);
    }
}
