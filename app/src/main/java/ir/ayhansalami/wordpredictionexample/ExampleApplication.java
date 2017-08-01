package ir.ayhansalami.wordpredictionexample;

import com.orm.SugarApp;
import com.orm.SugarContext;

/**
 * @author Ayhan Salami on 8/1/2017.
 *         Email: ayhan.irta@gmail.com
 *         Social Networks: ayhansalami
 */

public class ExampleApplication extends SugarApp {
    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(this);
    }
}
