package ch.dekuen.android.compass.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import java.util.function.Consumer;

public class CompassSensorEventListener implements SensorEventListener {
    private final Consumer<float[]> accelerationConsumer;
    private final Consumer<float[]> magneticConsumer;

    public CompassSensorEventListener(Consumer<float[]> accelerationConsumer, Consumer<float[]> magneticConsumer) {
        this.accelerationConsumer = accelerationConsumer;
        this.magneticConsumer = magneticConsumer;
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
            accelerationConsumer.accept(event.values);
        } else if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticConsumer.accept(event.values);
        } else {
            Log.e(getClass().getName(), "Unexpected sensor type");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }
}
