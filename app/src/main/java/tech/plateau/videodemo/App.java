package tech.plateau.videodemo;

import android.app.Application;

public class App extends Application {

    private static App INSTANCE = null;
    public static App getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        INSTANCE = this;
    }
}
