package com.example.onlinetest.mapper;

import com.example.onlinetest.dto.AnswerRequest;
import com.example.onlinetest.dto.QuestionRequest;
import com.example.onlinetest.model.Answer;
import com.example.onlinetest.model.Question;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class QuestionMapper {

    public Question toEntity(QuestionRequest request) {
        if (request == null) {
            return null;
        }
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
        if (request == null) {
            return null;
        }
        Answer answer = new Answer();
        answer.setText(request.text());
        answer.setIsCorrect(request.isCorrect() != null && request.isCorrect());
        return answer;
    }
}