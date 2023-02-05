package ch.dekuen.android.compass.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import java.util.function.Consumer;

public class CompassSensorEventListener implements SensorEventListener {
    private final Consumer<float[]> consumer;
    private final int sensorType;

    public CompassSensorEventListener(Consumer<float[]> consumer, int sensorType) {
        this.consumer = consumer;
        this.sensorType = sensorType;
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
        int type = sensor.getType();
        if (sensorType != type) {
            Log.e(getClass().getName(), "Unexpected sensor type " + type + ". Expected " + sensorType);
            return;
        }


        consumer.accept(event.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }
}
