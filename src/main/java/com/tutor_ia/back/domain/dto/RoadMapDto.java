package com.tutor_ia.back.domain.dto;

import lombok.Builder;

@Builder
public record RoadMapDto (
        String name,
        Integer progress
) { }
