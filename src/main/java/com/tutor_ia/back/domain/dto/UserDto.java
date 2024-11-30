package com.tutor_ia.back.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserDto (
    @NotBlank(message = "email cannot be blank")
    String email,

    @JsonProperty("user_name")
    @NotBlank(message = "user_name cannot be blank")
    String userName,

    @NotBlank(message = "password cannot be blank")
    String password
) {}
