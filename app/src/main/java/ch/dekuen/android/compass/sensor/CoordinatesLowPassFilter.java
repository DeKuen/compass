package ch.dekuen.android.compass.sensor;

import java.util.function.Consumer;

public class CoordinatesLowPassFilter {
    private final float oneMinusAlpha;
    private final Consumer<float[]> consumer;
    private final float alpha;
    private float[] measurements;

    public CoordinatesLowPassFilter(Consumer<float[]> consumer, float alpha) {
        this.consumer = consumer;
        this.alpha = alpha;
        oneMinusAlpha = 1 - alpha;
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

    float applyFilter(float before, float update) {
        return alpha * before + oneMinusAlpha * update;
    }
}
