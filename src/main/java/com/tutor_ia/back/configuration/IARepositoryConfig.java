package com.tutor_ia.back.configuration;

import com.tutor_ia.back.repository.IARepository;
import com.tutor_ia.back.repository.implementation.IARepositoryImpl;
import com.tutor_ia.back.repository.mock.IARepositoyMock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class IARepositoryConfig {

    @Bean
    @Profile("local")
    public IARepository IARepositoryMock() {
        return new IARepositoyMock();
    }

    @Bean
    @Profile("!local")
    public IARepository IARepository(@Qualifier("IAClient") WebClient iAClient) {
        return new IARepositoryImpl(iAClient);
    }
}
