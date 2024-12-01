package com.tutor_ia.back.domain;

import lombok.Builder;

@Builder
public record ChatResponse<T> (
    T response
) { }
