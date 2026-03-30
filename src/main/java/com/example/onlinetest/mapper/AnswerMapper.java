package com.example.onlinetest.mapper;

import com.example.onlinetest.dto.AnswerRequest;
import com.example.onlinetest.dto.AnswerResponse;
import com.example.onlinetest.model.Answer;
import org.springframework.stereotype.Component;

@Component
public class AnswerMapper {

    public Answer toEntity(AnswerRequest request) {
        Answer answer = new Answer();
        answer.setText(request.text());
        answer.setIsCorrect(request.isCorrect() != null && request.isCorrect());
        return answer;
    }

    public AnswerResponse toResponse(Answer answer) {
        return new AnswerResponse(
            answer.getId(),
            answer.getText(),
            answer.getIsCorrect()
        );
    }

    public void update(Answer answer, AnswerRequest request) {
        answer.setText(request.text());
        answer.setIsCorrect(request.isCorrect() != null && request.isCorrect());
    }
}