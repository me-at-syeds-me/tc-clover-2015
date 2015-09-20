package me.syeds.ahead;

import android.app.Application;

/**
 * Created by syedhaider on 9/20/15.
 */
public class DemoApplication extends Application {

    private static DemoApplication appContext;

    public DemoApplication() {
        appContext = this;
    }

    public static DemoApplication getAppContext() {
        return appContext;
    }
}