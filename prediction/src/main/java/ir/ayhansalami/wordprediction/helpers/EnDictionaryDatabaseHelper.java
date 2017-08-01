package ir.ayhansalami.wordprediction.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import ir.ayhansalami.wordprediction.Predictor;

/**
 * @author Ayhan Salami on 2/8/2017.
 *         Email: ayhan.irta@gmail.com
 *         Social Networks: ayhansalami
 */
public class EnDictionaryDatabaseHelper extends SQLiteOpenHelper {
    public static String DB_PATH = "/data/data/"+ Predictor.PACKAGE_NAME+"/databases/";
    public static String DB_NAME = "en.db";
    public static final int DB_VERSION = 1;
    public static final String TB_EN_DICTIONARY = "enDictionary";
    private SQLiteDatabase myDB;
    private Context context;

    public EnDictionaryDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    @Override
    public synchronized void close(){
        if(myDB!=null){
            myDB.close();
        }
        super.close();
    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();

        if (dbExist) {

        } else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e("tle99 - create", e.getMessage());
            }
        }
    }

    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        myDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    private boolean checkDataBase() {
        SQLiteDatabase tempDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            tempDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException e) {
            Log.e("tle99 - check", e.getMessage());
        }
        if (tempDB != null)
            tempDB.close();
        return tempDB != null ? true : false;
    }

    public void copyDataBase() throws IOException {
        try {
            InputStream myInput = context.getAssets().open(DB_NAME);
            String outputFileName = DB_PATH + DB_NAME;
            OutputStream myOutput = new FileOutputStream(outputFileName);
            byte[] buffer = new byte[1024];
            int length;
            while((length = myInput.read(buffer))>0){
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception e) {
            Log.e("tle99 - copyDatabase", e.getMessage());
        }
    }

    public List<String> predictCurrentWord(int number, String word) {
        List<String> words = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        try {
            cursor = db.rawQuery("SELECT DISTINCT NAME FROM " + TB_EN_DICTIONARY +" WHERE NAME LIKE '"+word+"%' AND NAME != '"+word+"' ORDER BY FREQUENCY DESC LIMIT "+number, null);
            if(cursor == null) return null;

            String name;
            cursor.moveToFirst();
            do {
                name = cursor.getString(0);
                words.add(name);
            } while (cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            Log.e("tle99", e.getMessage());
        }
        return words;
    }

    public void selectWord(String word) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("UPDATE " + TB_EN_DICTIONARY + " SET FREQUENCY=FREQUENCY+1 WHERE NAME='" + word + "'");
        } catch (Exception e) {
            Log.e("tle99", e.getMessage());
        }
        db.close();
    }

    public void addWord(String word) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("INSERT INTO " + TB_EN_DICTIONARY + " (_id, ID, NAME, FREQUENCY) VALUES (null, null, '"+word+"', 1)");
        } catch (Exception e) {
            Log.e("tle99", e.getMessage());
        }
        db.close();
    }

    public boolean wordExistInDictionary(String word) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        try {
            cursor = db.rawQuery("SELECT NAME FROM " + TB_EN_DICTIONARY +" WHERE NAME = '"+word+"'", null);
            if(cursor == null) {
                return false;
            }
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
            return false;
        } catch (Exception e) {
            return false;
        }
    }

}