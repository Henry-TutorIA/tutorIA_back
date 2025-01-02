package com.tutor_ia.back.repository;

import com.tutor_ia.back.domain.ChatResponse;

public interface IARepository {

    ChatResponse<Boolean> isValidTheme(String topic);

}
