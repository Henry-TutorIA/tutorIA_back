package com.tutor_ia.back.domain.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ScoreDto(
        Integer value,
        List<String> skills,
        String feedback,
        List<String> doneExercises,
        List<String> roadmap
) { }
