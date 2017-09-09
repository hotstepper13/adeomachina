package de.hotstepper13.adeomachina.vo;

/**
 * Created by fmuell on 04.09.17.
 */

public class TranslationVO {

    private int page;
    private String latin;
    private String word_case;
    private String declination;
    private String[] german;
    private String[] example;

    public TranslationVO(String csv_line) {
        //page;latin;case;declination;german1;german2;german3;german4;german5;german6;example1;example2;example3;example4
        //13 fields

        this.german = new String[6];
        this.example = new String[4];

        if(csv_line != null && !csv_line.equals("")) {
            if(!csv_line.startsWith("#")) {
                String[] values = csv_line.split(";", -1);
                if (values.length == 14) {
                    this.page = Integer.parseInt(values[0]);
                    this.latin = values[1];
                    this.word_case = values[2];
                    this.declination = values[3];
                    this.german[0] = values[4];
                    this.german[1] = values[5];
                    this.german[2] = values[6];
                    this.german[3] = values[7];
                    this.german[4] = values[8];
                    this.german[5] = values[9];
                    this.example[0] = values[10];
                    this.example[1] = values[11];
                    this.example[2] = values[12];
                    this.example[3] = values[13];
                } else {
                    System.out.println("ERROR: invalid text format. Expected 14 fields, but got : " + values.length + "\n" + csv_line);
                }
            }
        }

    }

    public int getPage() {
        return this.page;
    }

    public String getLatin() {
        return this.latin;
    }

    public String getWord_case() {
        return this.word_case;
    }

    public String getDeclination() {
        String result = "";
        if(!this.declination.equals("")) {
            result += "Deklination(en):\n" + this.declination + "\n\n";
        }
        return result;
    }

    public String getExamples() {
        String result = "";
            for (int i = 0; i < this.example.length; i++) {
                if(!this.example[i].equals("")) {
                    result += "- \"" + example[i] + "\"\n";
                }
            }
            if(result.length() > 0) {
                return "Beispiel(e):\n" + result;
            }
        return "";
    }

    public String getQuestion() {
        String question = this.latin;
        if(!this.word_case.equals("")) {
            question += " (" + this.word_case + ")";
        }
        return question;
    }

    public boolean checkAnswer(String answer) {
        Boolean result = false;
        if(!answer.equals("")) {
            for (int i = 0; i < this.german.length; i++) {
                if (answer.toLowerCase().trim().equals(this.german[i].toLowerCase().trim())) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    public String getAnswers() {
        String answers = "Mögliche richtige Antwort(en):\n";
        for (int i = 0; i < this.german.length; i++) {
            if(!this.german[i].equals("")) {
                answers += "- \"" + this.german[i].toLowerCase().trim() + "\"\n";
            }
        }
        return answers + "\n";
    }

}
