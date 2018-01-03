package innovationsquare.com.alternatebrain.utils;

import android.app.Application;

import innovationsquare.com.alternatebrain.activities.ExceptionManager;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by tariq on 9/9/2017.
 */

public class App extends Application {

    private static App instance;
    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        instance = this;
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionManager(this));
    }
}