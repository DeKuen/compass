package ch.dekuen.android.compass.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Arrays;
import java.util.Locale;

public class CompassSensorEventListener implements SensorEventListener {
    private final Handler handler;
    private final int sensorType;
    private final float alpha;
    private final float oneMinusAlpha;
    private float[] measurements;

    public CompassSensorEventListener(Handler handler, int sensorType, float alpha) {
        this.handler = handler;
        this.sensorType = sensorType;
        this.alpha = alpha;
        oneMinusAlpha = 1 - alpha;
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

        if(measurements == null) {
            measurements = Arrays.copyOf(event.values, event.values.length);
        } else {
            measurements[0] = applyFilter(measurements[0], event.values[0]);
            measurements[1] = applyFilter(measurements[1], event.values[1]);
            measurements[2] = applyFilter(measurements[2], event.values[2]);
        }
        Message message = handler.obtainMessage();
        message.obj = Arrays.copyOf(measurements, measurements.length);




        Thread thread = Thread.currentThread();
        String s = String.format(Locale.getDefault(),
                "%s : uses Thread name=%s, id=%d, priority=%d",
                this,
                thread.getName(),
                thread.getId(),
                thread.getPriority()
        );
        Log.d(getClass().getName(), s);

        handler.handleMessage(message);
    }

    private float applyFilter(float before, float update) {
        return alpha * before + oneMinusAlpha * update;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }
}
