package ch.dekuen.android.compass.sensor;

import android.hardware.SensorManager;
import android.util.Log;

import ch.dekuen.android.compass.AzimutListener;

public class AzimutCalculator {
    private final AzimutListener listener;
    private float[] accelerationMeasurements;
    private float[] magneticMeasurements;

    public AzimutCalculator(AzimutListener listener) {
        this.listener = listener;
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
        float[] orientation = new float[3];
        // orientation contains: azimut, pitch and roll
        SensorManager.getOrientation(matrixR, orientation);

        // Azimuth, angle of rotation about the -z axis.
        // Angle between the device's y axis and the magnetic north pole.
        // The range of values is -π to π.
        float azimut = orientation[0];

        // Pitch, angle of rotation about the x axis. Value is in orientation[1].
        // Angle between the screen and the ground. Tilting the device toward the ground creates a positive pitch angle.
        // The range of values is -π/2 to π/2.

        // Roll, angle of rotation about the y axis.
        // Tilting the left edge of the device toward the ground creates a positive roll angle.
        // The range of values is -π to π.
        float roll = orientation[2];

        boolean isDisplayUp = Math.abs(roll) <= Math.PI / 2;
        // send to listener
        listener.onNewAzimut(azimut, isDisplayUp);
    }
}
