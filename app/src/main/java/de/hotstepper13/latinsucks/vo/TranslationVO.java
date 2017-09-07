package de.hotstepper13.latinsucks.vo;

/**
 * Created by fmuell on 04.09.17.
 */

public class TranslationVO {

    private String question;
    private String answer;

    public TranslationVO(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
