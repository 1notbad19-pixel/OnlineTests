package com.example.onlinetest.mapper;

import com.example.onlinetest.dto.AnswerRequest;
import com.example.onlinetest.dto.QuestionRequest;
import com.example.onlinetest.model.Answer;
import com.example.onlinetest.model.Question;
import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class QuestionMapper {

    public Question toEntity(QuestionRequest request) {
        Question question = new Question();
        question.setText(request.text());
        question.setType(request.type());
        question.setPoints(request.points());

        if (request.answers() != null) {
            Set<Answer> answers = request.answers().stream()
                .map(this::toAnswerEntity)
                .collect(Collectors.toSet());
            question.setAnswers(answers);
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
}