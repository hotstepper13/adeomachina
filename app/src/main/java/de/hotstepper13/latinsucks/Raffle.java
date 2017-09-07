package de.hotstepper13.latinsucks;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import de.hotstepper13.latinsucks.vo.TranslationVO;

public class Raffle extends AppCompatActivity {
    TextView question;
    TextView highscore;
    TextView current;
    EditText answer;
    Button submit;

    private static final String PREFS_NAME = "TranslationHighscorePrefs";
    private static TranslationVO currentTranslation;
    private static ArrayList<TranslationVO> translations;
    public static int highscoreValue;
    public static int currentScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getTranslationsFromAssets();
        highscoreValue = this.getHighScore();
        currentTranslation = this.getRandomTranslation();


        setContentView(R.layout.activity_raffle);
        question = (TextView) findViewById(R.id.question);
        highscore = (TextView) findViewById(R.id.highscore);
        current = (TextView) findViewById(R.id.currentScores);
        answer = (EditText) findViewById(R.id.answer);
        submit = (Button) findViewById(R.id.check);


        highscore.setText(getHighScore()+"");
        question.setText(currentTranslation.getQuestion());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView question = (TextView) findViewById(R.id.question);
                TextView current = (TextView) findViewById(R.id.currentScores);
                EditText answer = (EditText) findViewById(R.id.answer);

                if (answer.getText().toString().toLowerCase().equals(currentTranslation.getAnswer().toLowerCase())) {
                    Toast.makeText(Raffle.this, "Richtig!", Toast.LENGTH_SHORT).show();
                    currentScore++;
                    if(currentScore > getHighScore()) {
                        setHighScore(currentScore);
                        highscore.setText(getHighScore()+"");
                    }
                    current.setText(currentScore+"");
                } else {
                    Toast.makeText(Raffle.this, "Leider falsch!\n (" + currentTranslation.getAnswer() + ")" , Toast.LENGTH_SHORT).show();
                    currentScore = 0;
                    current.setText("0");
                }
                currentTranslation = getRandomTranslation();
                question.setText(currentTranslation.getQuestion());
                answer.setText("");

            }
        });

    }

    private TranslationVO getRandomTranslation() {
        Random generator = new Random();
        int i = generator.nextInt(this.translations.size()-1);
        return this.translations.get(i);
    }

    private void getTranslationsFromAssets() {
        try {
            InputStreamReader is = new InputStreamReader(getAssets().open("translations.csv"));
            BufferedReader reader = new BufferedReader(is);
            reader.readLine();
            String line;
            StringTokenizer st = null;
            if(this.translations == null) {
                this.translations = new ArrayList<TranslationVO>();
            }
            while ((line = reader.readLine()) != null) {
                st = new StringTokenizer(line, ",");
                TranslationVO tvo = new TranslationVO(st.nextToken(),st.nextToken());
                Raffle.translations.add(tvo);
                tvo = null;
            }
        } catch (IOException ioe) {
            System.out.println("Error while reading translations! " + ioe.toString());
        }
    }

    private void setHighScore(int number) {
        SharedPreferences.Editor prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        prefs.putInt("highscore", number).apply();
    }

    private int getHighScore() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt("highscore", 0);
    }


}
