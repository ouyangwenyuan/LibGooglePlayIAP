package net.droidman.iapdemo;
import android.app.Application;
import android.content.Context;

public class App extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        //获取Context
        context = getApplicationContext();

    }

    //返回
    public static Context getContext() {
        return context;
    }
}
