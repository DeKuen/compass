package ch.dekuen.android.compass;

import android.hardware.SensorManager;

public final class AppConstants {
    public static final float LOW_PASS_FILTER_ALPHA = 0.97f;
    // SENSOR_DELAY_GAME for fast response, alternatively use SENSOR_DELAY_UI or SENSOR_DELAY_NORMAL
    static final int SAMPLING_PERIOD_US = SensorManager.SENSOR_DELAY_GAME;

    private AppConstants() {
    }
}
