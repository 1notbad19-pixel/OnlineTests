package com.example.onlinetest.mapper;

import com.example.onlinetest.dto.AnswerRequest;
import com.example.onlinetest.dto.QuestionRequest;
import com.example.onlinetest.dto.QuestionResponse;
import com.example.onlinetest.model.Answer;
import com.example.onlinetest.model.Question;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class QuestionMapper {

    public Question toEntity(QuestionRequest request) {
        Question question = new Question();
        question.setText(request.text());
        question.setType(request.type());
        question.setPoints(request.points());

        if (request.answers() != null) {
            List<Answer> answers = request.answers().stream()
                .map(this::toAnswerEntity)
                .toList();
            question.setAnswers(new ArrayList<>(answers));
            answers.forEach(answer -> answer.setQuestion(question));
        }
        return question;
    }

    private Answer toAnswerEntity(AnswerRequest request) {
        Answer answer = new Answer();
        answer.setText(request.text());
        answer.setIsCorrect(request.isCorrect() != null && request.isCorrect());
        return answer;
    }

    public QuestionResponse toResponse(Question question) {
        return new QuestionResponse(
            question.getId(),
            question.getText(),
            question.getType(),
            question.getPoints(),
            question.getAnswers().stream()
                .map(this::toAnswerResponse)
                .toList()
        );
    }

    private com.example.onlinetest.dto.AnswerResponse toAnswerResponse(Answer answer) {
        return new com.example.onlinetest.dto.AnswerResponse(
            answer.getId(),
            answer.getText(),
            answer.getIsCorrect()
        );
    }

    public void update(Question question, QuestionRequest request) {
        question.setText(request.text());
        question.setType(request.type());
        question.setPoints(request.points());
    }
}