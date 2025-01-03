package com.tutor_ia.back.repository.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutor_ia.back.domain.ChatResponse;
import com.tutor_ia.back.domain.User;
import com.tutor_ia.back.domain.dto.ChatRequestDto;
import com.tutor_ia.back.domain.dto.ScoreDto;
import com.tutor_ia.back.domain.dto.SkillsDto;
import com.tutor_ia.back.repository.IARepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

    public ChatResponse<Set<User.Chat.Practice>> leveling(User user, String theme, List<SkillsDto> skills) {
        var question = String.format("""
                Debes armar un array de objectos con la siguiente estructura
                "{
                  "question": "Instrucción",
                  "topic": {theme},
                  "done": false
                }"
                con ejercicios para evaluar a alguién que tiene las skills que tiene el usuario
                
                IMPORTANTE:
                - El campo "question" tiene que ser una instrucción clara y concisa para que el usuario pueda probar sus conocimientos siempre en español
                - El campo "done" de los objetos debe ser siempre el boolean "false"
                - El campo "topic" de los objetos debe ser siempre el skill que se está evaluando, por ejemplo si dentro de las skills del usuario son
                "arrays, funciones" y ese ejrcicio sirve para evaluar arrays debe decir "arrays" siempre en minuscula, en español, sin puntuaciones y plural
                - Deben ser la cantidad de ejercicios que creas suficientes para evaluar si el usuario sabe o no cada una de las skills sin generar
                mas de 2 ejercicios por cada skill, pero si pueden ser menos
                - La dificultad de los ejercicios debe corresponder con el "level" que tiene el usuario para esa skill
                - En caso de que alguna skill no tenga sentido con lo que busca aprender el usuario descartala
                - Si los ejercicios se parecen mucho descarta uno y genera otro mas representativo de la skill que descartaste el ejercicio
                - Todos los ejercicios deben darse teniendo en cuenta que el usuario está buscando aprender %s
                - Solo debes devolver los ejercicios, no debes agregar nada más ni entregar los resultados.
                """, theme);

        var chatRequest = ChatRequestDto.mapToChatRequestDtoWithSkills(user, theme, question, skills);

        return ask(chatRequest, new ParameterizedTypeReference<Set<User.Chat.Practice>>() {});
    }

    public ChatResponse<ScoreDto> evaluate(User user, String theme, List<User.Chat.Practice> exercises) {
        var question = String.format("""
                Debes evaluar los siguientes ejercicios, te los voy a pasar como un array de objetos donde el campo "question"
                es la consigna a completar del ejercicio, el campo "answer" es la solución que dió el usuario y el campo "topic"
                es la skill que se está evaluando con ese ejercicio, necesito que tu respuesta tenga el siguiente formato
                "{
                  "value": 96,
                  "doneExercises": ["topic1"],
                  "feedback": "",
                  "skills": []
                }"
                
                IMPORTANTE:
                - Se toma como contestado correctamente cada ejercicio que dando una puntación en cuanto la respuesta del ejercicio que sea entre
                0 y 100, esa puntuacion sea 70 o superior
                - El campo "value" tiene que ser una puntaución general entre 0 y 100 con el resultado para informarle al usuario como le fue con los ejercicios
                este campo debe ser representativo de cuan bien le fue al usuario en total con todas las preguntas, por lo que si la mitad estan sin contestar
                la puntación maxima solo puede ser 50, en caso de que alguno de los demas ejercicios tenga algun punto que se pueda mejorar puede bajar mas el puntaje
                - El campo "doneExercises" tiene que ser un array de strings, en el que cada lugar tiene una "question" que respondió correctamente el usuario
                tiene que estar escrita exactamente igual que como fue entregada ya que luego se hace un match automatico, por lo que cualquier diferencia se va a tomar
                como erroneo el ejercicio
                - El campo "feedback" debe ser una devoluación para darle al usuario, por lo que debe estar bien escrita, de una manera formal y ser pasiva y lo mas concisa
                posible, dando puntos que se pueden mejorar y puntos debiles con respecto a la pregunta del ejercicio, sin adicionar mas temas
                para que no haya mal entendidos con el usuario, en caso de que haya ejercicios sin completar, solo da un feedback de los que están con solución sin mencionar que 
                quedan ejercicios sin completar ni que son necesarios, solo el feedback de los que están con solución, enfocandote en si se puede mejorar la solución y como
                - El campo "skills" debe ser siempre un array vacio
                - Todos los ejercicios deben evaluarse teniendo en cuenta que el usuario está buscando aprender %s
                - Los ejercicios son los siguientes: %s
                - NO MENCIONES QUE QUEDAN EJERCICIOS SIN COMPLETAR, USA TODOS LOS EJERCICIOS PARA EL CAMPO "VALUE"  DANDO UN PUNTAJE TANTO CON EJERCICIOS
                 COMPLETO COMO INCOMPLETOS PERO EN EL CAMPO FEEDBACK DEBE ESTAR SOLO EL FEEDBACK DE LOS
                EJERCICIOS QUE TIENEN EL CAMPO "ANSWER" CON ALGUN DATO, ENFOCANDOTE EN LOS QUE ESTÁN MAL
                """, theme, Arrays.toString(exercises.toArray()));

        var chatRequest = ChatRequestDto.mapToChatRequestDto(user, theme, question);

        return ask(chatRequest, new ParameterizedTypeReference<ScoreDto>() {});
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
