package com.tutor_ia.back.domain.dto;

import lombok.Builder;

@Builder
public record TokenDto (
   String token,
   String username
) {}
