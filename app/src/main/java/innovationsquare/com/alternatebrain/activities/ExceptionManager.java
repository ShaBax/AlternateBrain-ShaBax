package innovationsquare.com.alternatebrain.activities;

import com.crashlytics.android.Crashlytics;

import innovationsquare.com.alternatebrain.utils.App;
import io.fabric.sdk.android.Fabric;

/**
 * Hanlde any uncaught exception and upload to Fabric/CrashLytics
 */
public class ExceptionManager implements Thread.UncaughtExceptionHandler {

    /**
     * The Ex.
     */
    protected Throwable ex = null;
    private App application;

    /**
     * Instantiates a new Exception manager.
     *
     * @param myApplication the my application
     */
    public ExceptionManager(App myApplication) {
        application = myApplication;
        Fabric.with(application, new Crashlytics());
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        this.ex = ex;
        Crashlytics.logException(ex);
    }
}
