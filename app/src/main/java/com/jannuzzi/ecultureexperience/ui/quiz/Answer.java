package com.jannuzzi.ecultureexperience.ui.quiz;

public class Answer {
    private String text;
    private boolean correct;

    public Answer(String text, boolean correct) {
        this.text = text;
        this.correct = correct;
    }

    public boolean isCorrect() {
        return correct;
    }

    public String getText() {
        return text;
    }
}
