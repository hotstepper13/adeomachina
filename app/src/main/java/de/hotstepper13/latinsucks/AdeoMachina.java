package de.hotstepper13.latinsucks;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.ThreadLocalRandom;

import de.hotstepper13.latinsucks.vo.TranslationVO;

public class AdeoMachina extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView question;
    TextView highscore;
    TextView current;
    EditText answer;
    Button submit;
    Button reset;
    Dialog wrongDialog;

    private static final String TRANSLATION_FILE = "adeo-norm-extract.csv";
    private static final String PREFS_NAME = "TranslationPrefs";
    private static int currentMaxPage;
    private static TranslationVO currentTranslation;
    private static ArrayList<ArrayList<TranslationVO>> translations;
    public static int highscoreValue;
    public static int currentScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getTranslationsFromAssets();
        highscoreValue = this.getHighScore();
        if(getPage() > 0) {
            currentMaxPage = getPage();
        } else {
            currentMaxPage = translations.size() - 1;
        }

        currentTranslation = this.getRandomTranslation();

        setContentView(R.layout.activity_adeomachina);
        question = (TextView) findViewById(R.id.question);
        highscore = (TextView) findViewById(R.id.highscore);
        current = (TextView) findViewById(R.id.currentScores);
        answer = (EditText) findViewById(R.id.answer);
        submit = (Button) findViewById(R.id.check);
        reset = (Button) findViewById(R.id.reset);
        wrongDialog = new Dialog(AdeoMachina.this);

        this.activateSpinner();

        highscore.setText(getHighScore() + "");
        question.setText("(" + currentTranslation.getPage() + ") " + currentTranslation.getQuestion());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer();
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
        
        answer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkAnswer();
                    handled = true;
                }
                return handled;
            }
        });
        
    }


    private void checkAnswer() {
        TextView question = (TextView) findViewById(R.id.question);
        TextView current = (TextView) findViewById(R.id.currentScores);
        EditText answer = (EditText) findViewById(R.id.answer);

        String theAnswer = answer.getText().toString();

        if (currentTranslation.checkAnswer(theAnswer)) {
            Toast.makeText(AdeoMachina.this, "Richtig!", Toast.LENGTH_SHORT).show();
            currentScore++;
            if(currentScore > getHighScore()) {
                setHighScore(currentScore);
                highscore.setText(getHighScore()+"");
            }
            current.setText(currentScore+"");
        } else {
            showWrongDialog(theAnswer);
            currentScore = 0;
            current.setText("0");
        }
        currentTranslation = getRandomTranslation();
        question.setText("(" + currentTranslation.getPage() + ") " + currentTranslation.getQuestion());
        answer.setText("");
    }

    private TranslationVO getRandomTranslation() {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int page = ThreadLocalRandom.current().nextInt(1, AdeoMachina.currentMaxPage + 1);
        int entry = ThreadLocalRandom.current().nextInt(0, AdeoMachina.translations.get(page).size());
        return AdeoMachina.translations.get(page).get(entry);
    }

    private void getTranslationsFromAssets() {
        try {
            InputStreamReader is = new InputStreamReader(getAssets().open(TRANSLATION_FILE));
            BufferedReader reader = new BufferedReader(is);
            reader.readLine();
            String line;
            StringTokenizer st = null;
            if(AdeoMachina.translations == null) {
                AdeoMachina.translations = new ArrayList<ArrayList<TranslationVO>>();
                AdeoMachina.translations.add(0,new ArrayList<TranslationVO>());
            }
            while ((line = reader.readLine()) != null ) {
                if(!line.startsWith("#")) {
                    TranslationVO tvo = new TranslationVO(line);
                    if(tvo.getPage() > 0) {
                        if(AdeoMachina.translations.size() > tvo.getPage() &&  AdeoMachina.translations.get(tvo.getPage()) != null ) {
                            AdeoMachina.translations.get(tvo.getPage()).add(tvo);
                        } else {
                            ArrayList<TranslationVO> altemp = new ArrayList<TranslationVO>();
                            altemp.add(tvo);
                            AdeoMachina.translations.add(tvo.getPage(),altemp);
                        }
                    }
                }
            }
        } catch (IOException ioe) {
            System.out.println("Error while reading translations! " + ioe.toString());
        }
    }

    private void showWrongDialog(String theAnswer) {
        wrongDialog.setContentView(R.layout.wrong_dialog);
        wrongDialog.getWindow().setBackgroundDrawableResource(R.color.transparentWhite);
        wrongDialog.setTitle("Das war leider falsch!");
        wrongDialog.setCancelable(true);
        TextView text = (TextView) wrongDialog.findViewById(R.id.TextView01);
        text.setText(formatAnswer(theAnswer) + currentTranslation.getAnswers() + currentTranslation.getDeclination() + currentTranslation.getExamples());
        Button button = (Button) wrongDialog.findViewById(R.id.Button01);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wrongDialog.hide();
            }
        });
        wrongDialog.show();
    }

    private String formatAnswer(String input) {
        String result = "Deine Antwort: \"" + input + "\"\n";
        return result + "\n";
    }

    private void setHighScore(int number) {
        SharedPreferences.Editor prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        prefs.putInt("highscore", number).apply();
    }

    private int getHighScore() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt("highscore", 0);
    }

    private void setPage(int number) {
        SharedPreferences.Editor prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        prefs.putInt("page", number).apply();
    }

    private int getPage() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt("page", translations.size()-1);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        setPage(pos+1);
        AdeoMachina.currentMaxPage = pos+1;
        AdeoMachina.currentTranslation = getRandomTranslation();
        question.setText("(" + currentTranslation.getPage() + ") " + currentTranslation.getQuestion());
        answer.setText("");
    }

    public void onNothingSelected(AdapterView<?> parent) {
        //do nothing
    }

    private void activateSpinner() {
        Spinner dropdown = (Spinner)findViewById(R.id.maxPages);
        String[] items = new String[translations.size()-1];
        for(int i=1;i<translations.size();i++) {
            items[i-1]="Seite " + i;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        dropdown.setSelection(adapter.getPosition("Seite " + currentMaxPage));
        dropdown.setOnItemSelectedListener(this);

    }

}
