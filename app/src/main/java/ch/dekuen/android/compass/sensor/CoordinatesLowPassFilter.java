package ch.dekuen.android.compass.sensor;

import java.util.function.Consumer;

public class CoordinatesLowPassFilter {
    static final float LOW_PASS_FILTER_ALPHA = 0.97f;
    public static final float LOW_PASS_FILTER_ONE_MINUS_ALPHA = 1 - LOW_PASS_FILTER_ALPHA;
    private final Consumer<float[]> consumer;
    private float[] measurements;

    public CoordinatesLowPassFilter(Consumer<float[]> consumer) {
        this.consumer = consumer;
    }

    public void onSensorChanged(float[] updates) {
        if(measurements == null) {
            measurements = updates.clone();
        } else {
            measurements[0] = applyFilter(measurements[0], updates[0]);
            measurements[1] = applyFilter(measurements[1], updates[1]);
            measurements[2] = applyFilter(measurements[2], updates[2]);
        }
        consumer.accept(measurements);
    }

    static float applyFilter(float before, float update) {
        return LOW_PASS_FILTER_ALPHA * before + LOW_PASS_FILTER_ONE_MINUS_ALPHA * update;
    }
}
