package com.tutor_ia.back.configuration;

import io.netty.handler.logging.LogLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient IAClient(@Value("${clients.ia.base-url}") String IABaseUrl) {
         return createWebClient(IABaseUrl);
    }

    public WebClient createWebClient(String baseUrl) {
        var httpClient = HttpClient.create()
                .followRedirect(true);
//                .wiretap("reactor.netty.http.client.HttpClient",
//                        LogLevel.DEBUG,
//                        AdvancedByteBufFormat.TEXTUAL);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
