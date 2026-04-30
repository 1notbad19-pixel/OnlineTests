package com.example.onlinetest.dto;

public record AnswerResponse(
    Long id,
    String text,
    Boolean isCorrect
) { }