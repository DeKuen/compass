package ch.dekuen.android.compass.sensor;

import android.hardware.SensorManager;
import android.util.Log;

import java.util.function.Consumer;

public class RotationMatrixCalculator {
    private final Consumer<float[]> consumer;
    private float[] accelerationMeasurements;
    private float[] magneticMeasurements;

    public RotationMatrixCalculator(Consumer<float[]> consumer) {
        this.consumer = consumer;
    }

    public void onAccelerationSensorChanged(float[] updates) {
        accelerationMeasurements = updates;
        calculate();
    }

    public void onMagneticSensorChanged(float[] updates) {
        magneticMeasurements = updates;
        calculate();
    }

    private void calculate() {
        if (accelerationMeasurements == null) {
            Log.i(getClass().getName(), "accelerationMeasurements is still null");
            return;
        } else if (magneticMeasurements == null) {
            Log.i(getClass().getName(), "magneticMeasurements is still null");
            return;
        }
        float[] matrixR = new float[9];
        boolean success = SensorManager.getRotationMatrix(matrixR, null, accelerationMeasurements, magneticMeasurements);
        if (!success) {
            Log.i(getClass().getName(), "could not calculate displayRotation matrix");
            return;
        }
        consumer.accept(matrixR);
    }
}
