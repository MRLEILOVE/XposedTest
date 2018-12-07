package www.leigq.com.xposedtest2.application;

import android.app.Application;
import android.content.Context;

/**
 * <p>
 *
 * @author: asus
 * @date: 2018-11-30 18:05
 */
public class MyApplication extends Application {
    private static Context mContext;
    @Override
    public void onCreate(){
        super.onCreate();
        mContext = getApplicationContext();
    }
    public static Context getGlobalContext(){
        return mContext;
    }
}