package ir.ayhansalami.wordpredictionexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ir.ayhansalami.wordprediction.Predictor;
import ir.ayhansalami.wordprediction.enums.LanguageEnum;
import ir.ayhansalami.wordprediction.listeners.OnPredictListener;

public class MainActivity extends AppCompatActivity {
    private Button learn;
    private TextView currentPredictions;
    private TextView nextPredictions;
    private EditText input;
    private RadioButton english, persian;
    private LanguageEnum currentLanguage = LanguageEnum.EN;
    private Predictor predictor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        predictor = new Predictor(this);
        learn = (Button)findViewById(R.id.learn);
        currentPredictions = (TextView)findViewById(R.id.currentWordPredictions);
        nextPredictions = (TextView)findViewById(R.id.nextWordPredictions);
        input = (EditText)findViewById(R.id.input);
        english = (RadioButton)findViewById(R.id.english);
        persian = (RadioButton)findViewById(R.id.persian);
        english.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    currentLanguage = LanguageEnum.EN;
                }
            }
        });
        persian.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    currentLanguage = LanguageEnum.FA;
                }
            }
        });
        learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getText() == null || input.getText().length() <= 0) {
                    return;
                }
                String inputText = input.getText().toString();
                predictor.learnSentence(currentLanguage, inputText);
                input.setText("");
                Toast.makeText(MainActivity.this, R.string.thank_you, Toast.LENGTH_LONG).show();
            }
        });
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input.getText() == null || input.getText().length() <= 0) {
                    return;
                }
                String inputText = input.getText().toString();
                Character lastChar = inputText.charAt(inputText.length()-1);
                if (lastChar == ' ') {//predict next word
                    predictor.predictNextWordAsync(currentLanguage, 5, inputText, new OnPredictListener() {
                        @Override
                        public void onPredict(List<String> words) {
                            nextPredictions.setText(words.toString());
                        }
                    });
                } else {//predict current word
                    String[] words = inputText.split("\\s+");
                    String lastWord = words[words.length-1];
                    predictor.predictCurrentWordAsync(currentLanguage, 5, lastWord, new OnPredictListener() {
                        @Override
                        public void onPredict(List<String> words) {
                            currentPredictions.setText(words.toString());
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
