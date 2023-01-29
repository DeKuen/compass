package ch.dekuen.android.compass;

import android.app.Application;
import android.os.StrictMode;

public class MyApp extends Application {

    public MyApp() {
        StrictMode.enableDefaults();
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .build());
    }

}