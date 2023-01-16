package ch.dekuen.android.compassapp.listener;

import android.hardware.SensorManager;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Optional;

public class AzimutService {
    Optional<Float> getAzimut(@NonNull float[] accelerationMeasurements, @NonNull float[] magneticMeasurements) {
        try {
            float[] matrixR = new float[9];
            boolean success = SensorManager.getRotationMatrix(matrixR, null, accelerationMeasurements, magneticMeasurements);
            if (!success) {
                return Optional.empty();
            }
            float[] orientation = new float[3];
            // orientation contains: azimut, pitch and roll
            SensorManager.getOrientation(matrixR, orientation);
            // get angle around the z-axis rotated
            float azimut = orientation[0];
            return Optional.of(azimut);
        } catch (RuntimeException e) {
            Log.e(getClass().getName(), "Error calculating azimut", e);
            return Optional.empty();
        }
    }
}