package com.tutor_ia.back.repository.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutor_ia.back.domain.ChatResponse;
import com.tutor_ia.back.domain.User;
import com.tutor_ia.back.domain.dto.ChatRequestDto;
import com.tutor_ia.back.domain.dto.SkillsDto;
import com.tutor_ia.back.repository.IARepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Log4j2
@Repository
public class IARepositoryImpl implements IARepository {

    private final WebClient iAClient;

    private final ObjectMapper objectMapper;

    public IARepositoryImpl(@Qualifier("IAClient") WebClient iAClient, ObjectMapper objectMapper) {
        this.iAClient = iAClient;
        this.objectMapper = objectMapper;
    }

    public ChatResponse isValidTheme(User user, String topic) {
        var question = String.format("""
                Debes revisar este tema "%s" y decidir si es posible crear una guía de estudio
                o si es un tema ambiguo que no se puede estudiar.
                IMPORTARTE:
                - NO COMPARTAS TUS PENSAMIENTOS NI COMENTARIOS. SOLO RESPONDE CON "true" O "false", SIEMPRE EN MINUSCULA
                Y SIN AGREGAR PUNTUACION NI ACENTOS.
                """, topic);
        var chatRequest = ChatRequestDto.mapToChatRequestDto(user, topic, question);

        return ask(chatRequest, new ParameterizedTypeReference<String>() {});
    }

    public ChatResponse<List<User.Chat.Practice>> leveling(User user, String theme, List<SkillsDto> skills) {
        var question = String.format("""
                Debes armar un array de objectos con la siguiente estructura
                "{
                  "question": "Instrucción",
                  "topic": {theme},
                  "done": false
                }"
                con ejercicios para evaluar a alguién que tiene las skills que tiene el usuario
                
                IMPORTANTE:
                - El campo "done" de los objetos debe ser siempre el boolean "false"
                - El campo "topic" de los objetos debe ser siempre el skill que se está evaluando, por ejemplo si dentro de las skills del usuario son
                "arrays, funciones" y ese ejrcicio sirve para evaluar arrays debe decir "arrays" siempre en minuscula, en ingles, sin puntuaciones y plural
                - Deben ser la cantidad de ejercicios que creas suficientes para evaluar si el usuario sabe o no cada una de las skills sin generar
                mas de 2 ejercicios por cada skill, pero si pueden ser menos
                - La dificultad de los ejercicios debe corresponder con el "level" que tiene el usuario para esa skill
                - En caso de que alguna skill no tenga sentido con lo que busca aprender el usuario descartala
                - Si los ejercicios se parecen mucho descarta uno y genera otro mas representativo de la skill que descartaste el ejercicio
                - Todos los ejercicios deben darse teniendo en cuenta que el usuario está buscando aprender %s
                """, theme);

        var chatRequest = ChatRequestDto.mapToChatRequestDtoWithSkills(user, theme, question, skills);

        return ask(chatRequest, new ParameterizedTypeReference<List<User.Chat.Practice>>() {});
    }

    private <T> ChatResponse<T> ask(ChatRequestDto request, ParameterizedTypeReference<T> typeReference) {
        var response =  iAClient.post()
                .uri("/chat")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ChatResponse<String>>() {})
                .block();

        T parsedResponse = null;
        String cleanedResponse = response.response().replace("```json", "").replace("```", "").trim();

        try {
            parsedResponse = objectMapper.readValue(cleanedResponse, objectMapper.getTypeFactory().constructType(typeReference.getType()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        log.error("RESPONSE: " + response);
        return new ChatResponse<>(parsedResponse);
    }

}
