package de.hotstepper13.latinsucks;

import android.app.Dialog;
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

public class AdeoMachina extends AppCompatActivity {
    TextView question;
    TextView highscore;
    TextView current;
    EditText answer;
    Button submit;
    Button reset;
    Dialog wrongDialog;

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


        setContentView(R.layout.activity_adeomachina);
        question = (TextView) findViewById(R.id.question);
        highscore = (TextView) findViewById(R.id.highscore);
        current = (TextView) findViewById(R.id.currentScores);
        answer = (EditText) findViewById(R.id.answer);
        submit = (Button) findViewById(R.id.check);
        reset = (Button) findViewById(R.id.reset);
        wrongDialog = new Dialog(AdeoMachina.this);

        highscore.setText(getHighScore()+"");
        question.setText(currentTranslation.getQuestion());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView question = (TextView) findViewById(R.id.question);
                TextView current = (TextView) findViewById(R.id.currentScores);
                EditText answer = (EditText) findViewById(R.id.answer);

                if (answer.getText().toString().toLowerCase().equals(currentTranslation.getAnswer().toLowerCase())) {
                    Toast.makeText(AdeoMachina.this, "Richtig!", Toast.LENGTH_SHORT).show();
                    currentScore++;
                    if(currentScore > getHighScore()) {
                        setHighScore(currentScore);
                        highscore.setText(getHighScore()+"");
                    }
                    current.setText(currentScore+"");
                } else {
                    showWrongDialog();
                    //Toast.makeText(AdeoMachina.this, "Leider falsch!\n (" + currentTranslation.getAnswer() + ")" , Toast.LENGTH_SHORT).show();
                    currentScore = 0;
                    current.setText("0");
                }
                currentTranslation = getRandomTranslation();
                question.setText(currentTranslation.getQuestion());
                answer.setText("");

            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setHighScore(0);
                highscore.setText(getHighScore()+"");
                currentScore = 0;
                current.setText(currentScore+"");
            }
        });
    }

    private TranslationVO getRandomTranslation() {
        Random generator = new Random();
        int i = generator.nextInt(AdeoMachina.translations.size()-1);
        return AdeoMachina.translations.get(i);
    }

    private void getTranslationsFromAssets() {
        try {
            InputStreamReader is = new InputStreamReader(getAssets().open("translations.csv"));
            BufferedReader reader = new BufferedReader(is);
            reader.readLine();
            String line;
            StringTokenizer st = null;
            if(AdeoMachina.translations == null) {
                AdeoMachina.translations = new ArrayList<TranslationVO>();
            }
            while ((line = reader.readLine()) != null) {
                st = new StringTokenizer(line, ",");
                TranslationVO tvo = new TranslationVO(st.nextToken(),st.nextToken());
                AdeoMachina.translations.add(tvo);
                tvo = null;
            }
        } catch (IOException ioe) {
            System.out.println("Error while reading translations! " + ioe.toString());
        }
    }

    private void showWrongDialog() {
        wrongDialog.setContentView(R.layout.wrong_dialog);
        wrongDialog.getWindow().setBackgroundDrawableResource(R.color.transparentWhite);
        wrongDialog.setTitle("Das war leider falsch!");
        wrongDialog.setCancelable(true);
        TextView text = (TextView) wrongDialog.findViewById(R.id.TextView01);
        text.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque nibh libero, eleifend eu urna ut, sodales sodales ante. Aliquam erat volutpat. Sed vitae lacinia diam. Aliquam ut nulla aliquam, fermentum turpis vitae, interdum tortor. Aliquam eleifend nisi odio. Donec malesuada purus ut tellus ultrices, id pulvinar mi mollis. Nulla mollis nisl vitae nunc fringilla porttitor. In faucibus imperdiet vestibulum. Cras sed hendrerit neque. Morbi faucibus nulla sed felis bibendum pretium eget sed turpis. Nam venenatis porta erat, non bibendum erat volutpat vitae. Sed convallis augue leo, ut euismod massa dictum pharetra. Nam tincidunt ultricies viverra. Integer vehicula ultricies mi efficitur fringilla. Aenean dictum sed orci vitae scelerisque. Nunc volutpat sem sit amet massa consequat, ac dapibus mauris rutrum.");
        Button button = (Button) wrongDialog.findViewById(R.id.Button01);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wrongDialog.hide();
            }
        });
        wrongDialog.show();
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
