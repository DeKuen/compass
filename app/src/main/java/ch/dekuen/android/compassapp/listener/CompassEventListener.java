package ch.dekuen.android.compassapp.listener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Optional;
import java.util.function.Consumer;

public class CompassEventListener implements SensorEventListener {

    private final Consumer<Float> azimutConsumer;

    private float[] accelerationMeasurements;
    private float[] magneticMeasurements;

    public CompassEventListener(Consumer<Float> azimutConsumer) {
        this.azimutConsumer = azimutConsumer;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerationMeasurements = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticMeasurements = event.values;
        }
        if (accelerationMeasurements != null && magneticMeasurements != null) {
            Optional<Float> optional = getAzimut();
            if(optional.isPresent())
            {
                Float azimut = optional.get();
                azimutConsumer.accept(azimut);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    private Optional<Float> getAzimut()
    {
        float[] R = new float[9];
        float[] I = new float[9];
        boolean success = SensorManager.getRotationMatrix(R, I, accelerationMeasurements, magneticMeasurements);
        if(!success)
        {
            return Optional.empty();
        }
        float[] orientation = new float[3];
        // orientation contains: azimut, pitch and roll
        SensorManager.getOrientation(R, orientation);
        // get angle around the z-axis rotated
        float azimut = orientation[0];
        return Optional.of(azimut);
    }
}
