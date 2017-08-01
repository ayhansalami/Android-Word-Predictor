package ir.ayhansalami.wordprediction.models;

import com.orm.SugarRecord;

/**
 * @author Ayhan Salami on 2/7/2017.
 *         Email: ayhan.irta@gmail.com
 *         Social Networks: ayhansalami
 */
public class FaBiGramModel extends SugarRecord{
    String firstWord;
    String secondWord;
    long frequency;

    public String getFirstWord() {
        return firstWord;
    }

    public void setFirstWord(String firstWord) {
        this.firstWord = firstWord;
    }

    public String getSecondWord() {
        return secondWord;
    }

    public void setSecondWord(String secondWord) {
        this.secondWord = secondWord;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }
}
