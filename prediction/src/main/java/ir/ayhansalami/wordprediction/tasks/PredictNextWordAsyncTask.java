package ir.ayhansalami.wordprediction.tasks;

import android.os.AsyncTask;

import java.util.List;

import ir.ayhansalami.wordprediction.Predictor;
import ir.ayhansalami.wordprediction.enums.LanguageEnum;
import ir.ayhansalami.wordprediction.listeners.OnPredictListener;

/**
 * @author Ayhan Salami on 2/11/2017.
 *         Email: ayhan.irta@gmail.com
 *         Social Networks: ayhansalami
 */
public class PredictNextWordAsyncTask extends AsyncTask<Void, Void, List<String>> {
    private Predictor predictor;
    private OnPredictListener listener;
    private String currentSentence;
    private int number;
    private LanguageEnum language;

    public PredictNextWordAsyncTask(Predictor predictor, OnPredictListener listener, String currentSentence, int number, LanguageEnum language) {
        this.predictor = predictor;
        this.listener = listener;
        this.currentSentence = currentSentence;
        this.number = number;
        this.language = language;
    }

    @Override
    protected List<String> doInBackground(Void... params) {
        try{
            return predictor.predictNextWord(language, number, currentSentence);
        }
        catch (Exception ex) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<String> words) {
        super.onPostExecute(words);
        listener.onPredict(words);
    }
}
