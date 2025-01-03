package com.tutor_ia.back.repository;

import com.tutor_ia.back.domain.ChatResponse;
import com.tutor_ia.back.domain.User;
import com.tutor_ia.back.domain.dto.SkillsDto;

import java.util.List;

public interface IARepository {

    ChatResponse<String> isValidTheme(User user, String topic);

    ChatResponse<List<User.Chat.Practice>> leveling(User user, String theme, List<SkillsDto> skills);

}
