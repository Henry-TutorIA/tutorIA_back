package com.tutor_ia.back.domain.dto;

import lombok.Builder;

@Builder
public record SkillsDto(
        String name,
        String level
)  { }
