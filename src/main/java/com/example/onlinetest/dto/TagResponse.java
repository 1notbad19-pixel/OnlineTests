package com.example.onlinetest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с данными тега")
public record TagResponse(
    @Schema(description = "ID тега", example = "1")
    Long id,

    @Schema(description = "Название тега", example = "java")
    String name
) { }