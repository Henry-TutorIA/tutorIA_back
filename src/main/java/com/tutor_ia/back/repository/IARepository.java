package com.tutor_ia.back.repository;

import com.tutor_ia.back.domain.ChatResponse;
import com.tutor_ia.back.domain.User;
import com.tutor_ia.back.domain.dto.ScoreDto;
import com.tutor_ia.back.domain.dto.SkillsDto;

import java.util.List;
import java.util.Set;

public interface IARepository {

    ChatResponse<String> isValidTheme(User user, String topic);

    ChatResponse<Set<User.Chat.Practice>> leveling(User user, String theme, List<SkillsDto> skills);

    ChatResponse<ScoreDto> evaluate(User user, String theme, List<User.Chat.Practice> exercises);
}
