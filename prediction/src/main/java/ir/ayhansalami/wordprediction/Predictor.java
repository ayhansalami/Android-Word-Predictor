package ir.ayhansalami.wordprediction;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ir.ayhansalami.wordprediction.enums.LanguageEnum;
import ir.ayhansalami.wordprediction.helpers.EnDictionaryDatabaseHelper;
import ir.ayhansalami.wordprediction.helpers.FaDictionaryDatabaseHelper;
import ir.ayhansalami.wordprediction.listeners.OnHttpCall;
import ir.ayhansalami.wordprediction.listeners.OnPredictListener;
import ir.ayhansalami.wordprediction.listeners.OnWordCheck;
import ir.ayhansalami.wordprediction.models.BiGramModel;
import ir.ayhansalami.wordprediction.models.FaBiGramModel;
import ir.ayhansalami.wordprediction.models.FaFourGramModel;
import ir.ayhansalami.wordprediction.models.FaTriGramModel;
import ir.ayhansalami.wordprediction.models.FaUniGramModel;
import ir.ayhansalami.wordprediction.models.FourGramModel;
import ir.ayhansalami.wordprediction.models.TriGramModel;
import ir.ayhansalami.wordprediction.models.UniGramModel;
import ir.ayhansalami.wordprediction.tasks.PredictCurrentWordAsyncTask;
import ir.ayhansalami.wordprediction.tasks.PredictNextWordAsyncTask;
import ir.ayhansalami.wordprediction.tasks.WordExistInDictionaryAsyncTask;

/**
 * @author Ayhan Salami on 2/7/2017.
 *         Email: ayhan.irta@gmail.com
 *         Social Networks: ayhansalami
 */
public class Predictor {
    private Context context;
    private FaDictionaryDatabaseHelper faDbHelper;
    private EnDictionaryDatabaseHelper enDbHelper;
    public static String PACKAGE_NAME;

    public Predictor(Context context) {
        this.context = context;
        PACKAGE_NAME = context.getPackageName();
        faDbHelper = new FaDictionaryDatabaseHelper(context);
        enDbHelper = new EnDictionaryDatabaseHelper(context);
        try {
            faDbHelper.createDataBase();
            enDbHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Sync Functions
    public void learnSentence(LanguageEnum language, String sentence) {
        sentence = sentence.toLowerCase().trim();
        if (language == LanguageEnum.EN) {
            learnEnSentence(sentence);
        } else if (language == LanguageEnum.FA){
            learnFaSentence(sentence);
        }
    }
    public List<String> predictCurrentWord (LanguageEnum language, int number, String word) {
        word = word.toLowerCase().trim();
        if (language == LanguageEnum.FA) {
            return predictCurrentFaWord(number, word);
        } else if (language == LanguageEnum.EN) {
            return predictCurrentEnWord(number, word);
        }
        return new LinkedList<>();
    }
    public void selectWord(LanguageEnum language, String word) {
        word = word.toLowerCase().trim();
        if (language == LanguageEnum.FA) {
            selectWordFa(word);
        } else if (language == LanguageEnum.EN) {
            selectWordEn(word);
        }
    }
    public void addWord(LanguageEnum language, String word) {
        word = word.toLowerCase().trim();
        if (language == LanguageEnum.EN) {
            enDbHelper.addWord(word);
        } else if (language == LanguageEnum.FA) {
            faDbHelper.addWord(word);
        }
    }
    public boolean isWordExistInDictionary(LanguageEnum language, String word) {
        word = word.toLowerCase().trim();
        if (language == LanguageEnum.EN) {
            return enDbHelper.wordExistInDictionary(word);
        } else if (language == LanguageEnum.FA) {
            return faDbHelper.wordExistInDictionary(word);
        }
        return false;
    }
    public List<String> predictNextWord(LanguageEnum language, int number, String sentence) {
        sentence = sentence.toLowerCase().trim();
        if (language ==  LanguageEnum.EN) {
            return predictNextEnWord(number, sentence);
        } else if(language == LanguageEnum.FA) {
            return predictNextFaWord(number, sentence);
        }
        return new LinkedList<>();
    }

    //Async Functions
    public void predictCurrentWordAsync (LanguageEnum language, int number, String word, OnPredictListener onPredictListener) {
        PredictCurrentWordAsyncTask asyncTask = new PredictCurrentWordAsyncTask(this,onPredictListener,word,number,language);
        asyncTask.execute();
    }
    public void predictNextWordAsync (LanguageEnum language, int number, String sentence, OnPredictListener onPredictListener) {
        PredictNextWordAsyncTask asyncTask = new PredictNextWordAsyncTask(this,onPredictListener,sentence,number,language);
        asyncTask.execute();
    }
    public void isWordExistInDictionaryAsync(LanguageEnum language, String word, OnWordCheck onWordCheck) {
        WordExistInDictionaryAsyncTask asyncTask = new WordExistInDictionaryAsyncTask(this,onWordCheck,word,language);
        asyncTask.execute();
    }

    private List<String> predictNextEnWord(int number, String sentence) {
        if (sentence == null) {
            return new LinkedList<>();
        }
        List<String> words = Arrays.asList(sentence.split("\\s+"));
        List<String> predictedWords = new LinkedList<>();
        if (words.size() >= 3) {//can use four gram
            List<FourGramModel> fourGramPredictedWords = FourGramModel.find(FourGramModel.class, "first_word = ? AND second_word = ? AND third_word = ?", new String[]{words.get(words.size()-3),
                    words.get(words.size()-2), words.get(words.size()-1)}, null, "frequency DESC", null);
            for (FourGramModel fourGram: fourGramPredictedWords) {
                if (predictedWords.size() < number && !predictedWords.contains(fourGram.getFourthWord())) {
                    predictedWords.add(fourGram.getFourthWord());
                } else if (predictedWords.size() >= number) {
                    return predictedWords;
                }
            }
        }
        if (words.size() >= 2 && predictedWords.size() < number) {//can use tri gram
            List<TriGramModel> triGramPredictedWords = TriGramModel.find(TriGramModel.class, "first_word = ? AND second_word = ?", new String[]{words.get(words.size()-2),
                    words.get(words.size()-1)}, null, "frequency DESC", null);
            for (TriGramModel triGram: triGramPredictedWords) {
                if (predictedWords.size() < number && !predictedWords.contains(triGram.getThirdWord())) {
                    predictedWords.add(triGram.getThirdWord());
                } else if (predictedWords.size() >= number) {
                    return predictedWords;
                }
            }
        }
        if (words.size() >= 1 && predictedWords.size() < number) {//can use bi gram
            List<BiGramModel> biGramPredictedWords = BiGramModel.find(BiGramModel.class, "first_word = ?", new String[]{words.get(words.size()-1)},
                    null, "frequency DESC", null);
            for (BiGramModel biGram: biGramPredictedWords) {
                if (predictedWords.size() < number && !predictedWords.contains(biGram.getSecondWord())) {
                    predictedWords.add(biGram.getSecondWord());
                } else if (predictedWords.size() >= number) {
                    return predictedWords;
                }
            }
        }
        if (predictedWords.size() < number) {//can use uni gram
            List<UniGramModel> uniGramPredictedWords = UniGramModel.find(UniGramModel.class,null,null,null,"frequency DESC",null);
            for (UniGramModel uniGram: uniGramPredictedWords) {
                if (predictedWords.size() < number && !predictedWords.contains(uniGram.getWord())) {
                    predictedWords.add(uniGram.getWord());
                } else if (predictedWords.size() >= number) {
                    return predictedWords;
                }
            }
        }
        return predictedWords;
    }
    private List<String> predictNextFaWord(int number, String sentence) {
        if (sentence == null) {
            return new LinkedList<>();
        }
        List<String> words = Arrays.asList(sentence.split("\\s+"));
        List<String> predictedWords = new LinkedList<>();
        if (words.size() >= 3) {//can use four gram
            List<FaFourGramModel> fourGramPredictedWords = FaFourGramModel.find(FaFourGramModel.class, "first_word = ? AND second_word = ? AND third_word = ?", new String[]{words.get(words.size()-3),
                    words.get(words.size()-2), words.get(words.size()-1)}, null, "frequency DESC", null);
            for (FaFourGramModel fourGram: fourGramPredictedWords) {
                if (predictedWords.size() < number && !predictedWords.contains(fourGram.getFourthWord())) {
                    predictedWords.add(fourGram.getFourthWord());
                } else if (predictedWords.size() >= number) {
                    return predictedWords;
                }
            }
        }
        if (words.size() >= 2 && predictedWords.size() < number) {//can use tri gram
            List<FaTriGramModel> triGramPredictedWords = FaTriGramModel.find(FaTriGramModel.class, "first_word = ? AND second_word = ?", new String[]{words.get(words.size()-2),
                    words.get(words.size()-1)}, null, "frequency DESC", null);
            for (FaTriGramModel triGram: triGramPredictedWords) {
                if (predictedWords.size() < number && !predictedWords.contains(triGram.getThirdWord())) {
                    predictedWords.add(triGram.getThirdWord());
                } else if (predictedWords.size() >= number) {
                    return predictedWords;
                }
            }
        }
        if (words.size() >= 1 && predictedWords.size() < number) {//can use bi gram
            List<FaBiGramModel> biGramPredictedWords = FaBiGramModel.find(FaBiGramModel.class, "first_word = ?", new String[]{words.get(words.size()-1)},
                    null, "frequency DESC", null);
            for (FaBiGramModel biGram: biGramPredictedWords) {
                if (predictedWords.size() < number && !predictedWords.contains(biGram.getSecondWord())) {
                    predictedWords.add(biGram.getSecondWord());
                } else if (predictedWords.size() >= number) {
                    return predictedWords;
                }
            }
        }
        if (predictedWords.size() < number) {//can use uni gram
            List<FaUniGramModel> uniGramPredictedWords = FaUniGramModel.find(FaUniGramModel.class,null,null,null,"frequency DESC",null);
            for (FaUniGramModel uniGram: uniGramPredictedWords) {
                if (predictedWords.size() < number && !predictedWords.contains(uniGram.getWord())) {
                    predictedWords.add(uniGram.getWord());
                } else if (predictedWords.size() >= number) {
                    return predictedWords;
                }
            }
        }
        return predictedWords;
    }
    private void learnEnSentence(String sentence) {
        List<String> sentences = new LinkedList<>();
        if (sentence.contains(".")) {
            sentences.addAll(Arrays.asList(sentence.split("\\.")));
        } else {
            sentences.add(sentence);
        }
        for (String singleSentence : sentences) {
            singleSentence = singleSentence.replaceAll("[^a-zA-Z0-9\\s]", "");
            //UniGram
            List<String> uniGrams = NGramTokenizer.ngrams(1, singleSentence);
            for(String uniGram:uniGrams) {
                List<UniGramModel> uniGramModelList = UniGramModel.find(UniGramModel.class, "word = ?", new String[]{uniGram});
                if (uniGramModelList != null && uniGramModelList.size() == 1) {
                    UniGramModel uniGramModel = uniGramModelList.get(0);
                    uniGramModel.setFrequency(uniGramModel.getFrequency()+1);
                    uniGramModel.save();
                } else if (uniGramModelList == null || uniGramModelList.size() == 0) {
                    UniGramModel uniGramModel = new UniGramModel();
                    uniGramModel.setWord(uniGram);
                    uniGramModel.setFrequency(1);
                    uniGramModel.save();
                }
            }
            //BiGram
            List<String> biGrams = NGramTokenizer.ngrams(2, singleSentence);
            for(String biGram:biGrams) {
                if (biGram == null || !biGram.contains(" ")) {
                    continue;
                }
                String[] biGramParts = biGram.split("\\s+");
                List<BiGramModel> biGramModelList = BiGramModel.find(BiGramModel.class, "first_word = ? AND second_word = ?", new String[]{biGramParts[0], biGramParts[1]});
                if (biGramModelList != null && biGramModelList.size() == 1) {
                    BiGramModel biGramModel = biGramModelList.get(0);
                    biGramModel.setFrequency(biGramModel.getFrequency()+1);
                    biGramModel.save();
                } else if (biGramModelList == null || biGramModelList.size() == 0) {
                    BiGramModel biGramModel = new BiGramModel();
                    biGramModel.setFirstWord(biGramParts[0]);
                    biGramModel.setSecondWord(biGramParts[1]);
                    biGramModel.setFrequency(1);
                    biGramModel.save();
                }
            }
            //TriGram
            List<String> triGrams = NGramTokenizer.ngrams(3, singleSentence);
            for(String triGram:triGrams) {
                if (triGram == null || !triGram.contains(" ")) {
                    continue;
                }
                String[] triGramParts = triGram.split("\\s+");
                List<TriGramModel> triGramModelList = TriGramModel.find(TriGramModel.class, "first_word = ? AND second_word = ? AND third_word = ?", new String[]{triGramParts[0], triGramParts[1], triGramParts[2]});
                if (triGramModelList != null && triGramModelList.size() == 1) {
                    TriGramModel triGramModel = triGramModelList.get(0);
                    triGramModel.setFrequency(triGramModel.getFrequency()+1);
                    triGramModel.save();
                } else if (triGramModelList == null || triGramModelList.size() == 0) {
                    TriGramModel triGramModel = new TriGramModel();
                    triGramModel.setFirstWord(triGramParts[0]);
                    triGramModel.setSecondWord(triGramParts[1]);
                    triGramModel.setThirdWord(triGramParts[2]);
                    triGramModel.setFrequency(1);
                    triGramModel.save();
                }
            }
            //FourGram
            List<String> fourGrams = NGramTokenizer.ngrams(4, singleSentence);
            for(String fourGram:fourGrams) {
                if (fourGram == null || !fourGram.contains(" ")) {
                    continue;
                }
                String[] fourGramParts = fourGram.split("\\s+");
                List<FourGramModel> fourGramModelList = FourGramModel.find(FourGramModel.class, "first_word = ? AND second_word = ? AND third_word = ? AND fourth_word = ?", new String[]{fourGramParts[0], fourGramParts[1], fourGramParts[2], fourGramParts[3]});
                if (fourGramModelList != null && fourGramModelList.size() == 1) {
                    FourGramModel fourGramModel = fourGramModelList.get(0);
                    fourGramModel.setFrequency(fourGramModel.getFrequency()+1);
                    fourGramModel.save();
                } else if (fourGramModelList == null || fourGramModelList.size() == 0) {
                    FourGramModel fourGramModel = new FourGramModel();
                    fourGramModel.setFirstWord(fourGramParts[0]);
                    fourGramModel.setSecondWord(fourGramParts[1]);
                    fourGramModel.setThirdWord(fourGramParts[2]);
                    fourGramModel.setFourthWord(fourGramParts[3]);
                    fourGramModel.setFrequency(1);
                    fourGramModel.save();
                }
            }
        }
    }
    private void learnFaSentence(String sentence) {
        List<String> sentences = new LinkedList<>();
        if (sentence.contains(".")) {
            sentences.addAll(Arrays.asList(sentence.split("\\.")));
        } else {
            sentences.add(sentence);
        }
        for (String singleSentence : sentences) {
            //UniGram
            List<String> uniGrams = NGramTokenizer.ngrams(1, singleSentence);
            for(String uniGram:uniGrams) {
                List<FaUniGramModel> uniGramModelList = FaUniGramModel.find(FaUniGramModel.class, "word = ?", new String[]{uniGram});
                if (uniGramModelList != null && uniGramModelList.size() == 1) {
                    FaUniGramModel uniGramModel = uniGramModelList.get(0);
                    uniGramModel.setFrequency(uniGramModel.getFrequency()+1);
                    uniGramModel.save();
                } else if (uniGramModelList == null || uniGramModelList.size() == 0) {
                    FaUniGramModel uniGramModel = new FaUniGramModel();
                    uniGramModel.setWord(uniGram);
                    uniGramModel.setFrequency(1);
                    uniGramModel.save();
                }
            }
            //BiGram
            List<String> biGrams = NGramTokenizer.ngrams(2, singleSentence);
            for(String biGram:biGrams) {
                if (biGram == null || !biGram.contains(" ")) {
                    continue;
                }
                String[] biGramParts = biGram.split("\\s+");
                List<FaBiGramModel> biGramModelList = FaBiGramModel.find(FaBiGramModel.class, "first_word = ? AND second_word = ?", new String[]{biGramParts[0], biGramParts[1]});
                if (biGramModelList != null && biGramModelList.size() == 1) {
                    FaBiGramModel biGramModel = biGramModelList.get(0);
                    biGramModel.setFrequency(biGramModel.getFrequency()+1);
                    biGramModel.save();
                } else if (biGramModelList == null || biGramModelList.size() == 0) {
                    FaBiGramModel biGramModel = new FaBiGramModel();
                    biGramModel.setFirstWord(biGramParts[0]);
                    biGramModel.setSecondWord(biGramParts[1]);
                    biGramModel.setFrequency(1);
                    biGramModel.save();
                }
            }
            //TriGram
            List<String> triGrams = NGramTokenizer.ngrams(3, singleSentence);
            for(String triGram:triGrams) {
                if (triGram == null || !triGram.contains(" ")) {
                    continue;
                }
                String[] triGramParts = triGram.split("\\s+");
                List<FaTriGramModel> triGramModelList = FaTriGramModel.find(FaTriGramModel.class, "first_word = ? AND second_word = ? AND third_word = ?", new String[]{triGramParts[0], triGramParts[1], triGramParts[2]});
                if (triGramModelList != null && triGramModelList.size() == 1) {
                    FaTriGramModel triGramModel = triGramModelList.get(0);
                    triGramModel.setFrequency(triGramModel.getFrequency()+1);
                    triGramModel.save();
                } else if (triGramModelList == null || triGramModelList.size() == 0) {
                    FaTriGramModel triGramModel = new FaTriGramModel();
                    triGramModel.setFirstWord(triGramParts[0]);
                    triGramModel.setSecondWord(triGramParts[1]);
                    triGramModel.setThirdWord(triGramParts[2]);
                    triGramModel.setFrequency(1);
                    triGramModel.save();
                }
            }
            //FourGram
            List<String> fourGrams = NGramTokenizer.ngrams(4, singleSentence);
            for(String fourGram:fourGrams) {
                if (fourGram == null || !fourGram.contains(" ")) {
                    continue;
                }
                String[] fourGramParts = fourGram.split("\\s+");
                List<FaFourGramModel> fourGramModelList = FaFourGramModel.find(FaFourGramModel.class, "first_word = ? AND second_word = ? AND third_word = ? AND fourth_word = ?", new String[]{fourGramParts[0], fourGramParts[1], fourGramParts[2], fourGramParts[3]});
                if (fourGramModelList != null && fourGramModelList.size() == 1) {
                    FaFourGramModel fourGramModel = fourGramModelList.get(0);
                    fourGramModel.setFrequency(fourGramModel.getFrequency()+1);
                    fourGramModel.save();
                } else if (fourGramModelList == null || fourGramModelList.size() == 0) {
                    FaFourGramModel fourGramModel = new FaFourGramModel();
                    fourGramModel.setFirstWord(fourGramParts[0]);
                    fourGramModel.setSecondWord(fourGramParts[1]);
                    fourGramModel.setThirdWord(fourGramParts[2]);
                    fourGramModel.setFourthWord(fourGramParts[3]);
                    fourGramModel.setFrequency(1);
                    fourGramModel.save();
                }
            }
        }
    }
    private List<String> predictCurrentFaWord(int number, String word) {
        return faDbHelper.predictCurrentWord(number, word);
    }
    private List<String> predictCurrentEnWord(int number, String word) {
        return enDbHelper.predictCurrentWord(number, word);
    }
    private void selectWordFa(String word) {
        faDbHelper.selectWord(word);
    }
    private void selectWordEn(String word) {
        enDbHelper.selectWord(word);
    }
}
