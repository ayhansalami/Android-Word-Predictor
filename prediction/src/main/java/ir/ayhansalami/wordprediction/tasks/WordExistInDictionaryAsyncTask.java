package ir.ayhansalami.wordprediction.tasks;

import android.os.AsyncTask;

import java.util.List;

import ir.ayhansalami.wordprediction.Predictor;
import ir.ayhansalami.wordprediction.enums.LanguageEnum;
import ir.ayhansalami.wordprediction.listeners.OnPredictListener;
import ir.ayhansalami.wordprediction.listeners.OnWordCheck;

/**
 * @author Ayhan Salami on 2/11/2017.
 *         Email: ayhan.irta@gmail.com
 *         Social Networks: ayhansalami
 */
public class WordExistInDictionaryAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private Predictor predictor;
    private OnWordCheck listener;
    private String currentWord;
    private LanguageEnum language;

    public WordExistInDictionaryAsyncTask(Predictor predictor, OnWordCheck listener, String currentWord, LanguageEnum language) {
        this.predictor = predictor;
        this.listener = listener;
        this.currentWord = currentWord;
        this.language = language;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try{
            return predictor.isWordExistInDictionary(language, currentWord);
        }
        catch (Exception ex) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean exist) {
        super.onPostExecute(exist);
        listener.onWordCheck(exist);
    }
}
