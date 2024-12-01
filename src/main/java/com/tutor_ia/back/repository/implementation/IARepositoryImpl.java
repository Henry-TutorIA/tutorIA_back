package com.tutor_ia.back.repository.implementation;

import com.tutor_ia.back.domain.ChatResponse;
import com.tutor_ia.back.repository.IARepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

@Repository
public class IARepositoryImpl implements IARepository {

    private final WebClient iAClient;

    public IARepositoryImpl(@Qualifier("IAClient") WebClient iAClient) {
        this.iAClient = iAClient;
    }

    public ChatResponse<Boolean> checkTheme(String topic) {
        return iAClient.get()
                .uri("/check-theme/%s", topic)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block();
    }
}
