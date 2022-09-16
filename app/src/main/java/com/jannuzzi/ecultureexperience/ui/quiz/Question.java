package com.jannuzzi.ecultureexperience.ui.quiz;

import java.util.List;

public class Question {
     private String questionText;
    private List<Answer> answers;

    public Question(String questionText, List<Answer> answers) {
        this.questionText = questionText;
        this.answers = answers;
    }

    public String getQuestionText() {
        return questionText;
    }

    public Answer getCorrectAnswer() {
        for (Answer answer: answers) {
            if (answer.isCorrect())
                return answer;
        }
        return null;
    }

    public List<Answer> getAnswers() {
        return answers;
    }
}
