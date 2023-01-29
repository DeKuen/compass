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
        boolean isPhoneFacingUp = accelerationMeasurements[2] >= 0;
        float[] matrixR = new float[9];
        boolean success = SensorManager.getRotationMatrix(matrixR, null, accelerationMeasurements, magneticMeasurements);
        if (!success) {
            Log.i(getClass().getName(), "could not calculate rotation matrix");
            return;
        }
        float[] orientation = new float[3];
        // orientation contains: azimut, pitch and roll
        SensorManager.getOrientation(matrixR, orientation);
        // get angle around the z-axis rotated
        float azimut = orientation[0];
        // send to listener
        listener.onNewAzimut(azimut, isPhoneFacingUp);
    }
}
