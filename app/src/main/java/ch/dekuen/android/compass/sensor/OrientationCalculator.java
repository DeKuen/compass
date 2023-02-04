package ch.dekuen.android.compass.sensor;

import android.hardware.SensorManager;

import java.util.function.Consumer;

public class OrientationCalculator {
    private final Consumer<float[]> consumer;

    public OrientationCalculator(Consumer<float[]> consumer) {
        this.consumer = consumer;
    }

    public void calculate(float[] matrixR) {

        float[] orientation = new float[3];
        // orientation contains: azimut, pitch and roll
        SensorManager.getOrientation(matrixR, orientation);
        // send to consumer
        consumer.accept(orientation);
    }
}
