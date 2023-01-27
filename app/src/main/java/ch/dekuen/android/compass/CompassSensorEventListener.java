package ch.dekuen.android.compass;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.function.Consumer;

public class CompassSensorEventListener implements SensorEventListener {
    private final Consumer<Float> azimutConsumer;
    private float[] accelerationMeasurements;
    private float[] magneticMeasurements;

    public CompassSensorEventListener(Consumer<Float> azimutConsumer) {
        this.azimutConsumer = azimutConsumer;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event == null) {
            Log.e(getClass().getName(), "SensorEvent is null");
            return;
        }
        Sensor sensor = event.sensor;
        if(sensor == null) {
            Log.e(getClass().getName(), "Sensor is null");
            return;
        }
        int sensorType = sensor.getType();
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            accelerationMeasurements = event.values;
        } else if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticMeasurements = event.values;
        } else {
            Log.e(getClass().getName(), "Unexpected sensor type");
            return;
        }
        if (accelerationMeasurements == null) {
            Log.i(getClass().getName(), "accelerationMeasurements is null");
            return;
        } else if (magneticMeasurements == null) {
            Log.i(getClass().getName(), "magneticMeasurements is null");
            return;
        }
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
        azimutConsumer.accept(azimut);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }
}
