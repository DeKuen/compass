package ch.dekuen.android.compass.azimut;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import java.util.Optional;
import java.util.function.Consumer;

public class CompassSensorEventListener implements SensorEventListener {
    private CalculateAzimutService calculateAzimutService = new CalculateAzimutService();
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
        Optional<Float> optional = calculateAzimutService.calculateAzimut(accelerationMeasurements, magneticMeasurements);
        if (optional.isPresent()) {
            Float azimut = optional.get();
            azimutConsumer.accept(azimut);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    void setAzimutService(CalculateAzimutService calculateAzimutService) {
        this.calculateAzimutService = calculateAzimutService;
    }
}
