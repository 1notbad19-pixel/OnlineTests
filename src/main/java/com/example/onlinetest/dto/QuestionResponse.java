package com.example.onlinetest.dto;

import java.util.List;

public record QuestionResponse(
    Long id,
    String text,
    String type,
    Integer points,
    List<AnswerResponse> answers
) { }