package com.tutor_ia.back.repository.mock;

import com.tutor_ia.back.domain.ChatResponse;
import com.tutor_ia.back.repository.IARepository;

public class IARepositoyMock implements IARepository {

    @Override
    public ChatResponse<Boolean> isValidTheme(String topic) {
        return new ChatResponse<>(true);
    }

}
