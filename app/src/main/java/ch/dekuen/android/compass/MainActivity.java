package ch.dekuen.android.compass;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.function.Supplier;

import ch.dekuen.android.compass.sensor.AzimutCalculator;
import ch.dekuen.android.compass.sensor.CompassSensorEventListener;
import ch.dekuen.android.compass.sensor.CoordinatesLowPassFilter;
import ch.dekuen.android.compass.view.CompassImageViewUpdater;
import ch.dekuen.android.compass.view.CompassTextViewUpdater;
import ch.dekuen.android.compass.view.CompassViewOrientationCorrector;

public class MainActivity extends Activity {

    // SENSOR_DELAY_GAME for fast response, alternatively use SENSOR_DELAY_UI or SENSOR_DELAY_NORMAL
    private static final int SAMPLING_PERIOD_US = SensorManager.SENSOR_DELAY_GAME;

    private CompassSensorEventListener accelerationSensorEventListener;
    private CompassSensorEventListener magneticSensorEventListener;

    // device sensor manager
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AzimutListener azimutListener = getAzimutListener();
        AzimutCalculator azimutCalculator = new AzimutCalculator(azimutListener);
        CoordinatesLowPassFilter accelerationLPF = new CoordinatesLowPassFilter(azimutCalculator::onAccelerationSensorChanged);
        CoordinatesLowPassFilter magneticLPF = new CoordinatesLowPassFilter(azimutCalculator::onMagneticSensorChanged);
        accelerationSensorEventListener = new CompassSensorEventListener(accelerationLPF::onSensorChanged, Sensor.TYPE_ACCELEROMETER);
        magneticSensorEventListener = new CompassSensorEventListener(magneticLPF::onSensorChanged, Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @NonNull
    private AzimutListener getAzimutListener() {
        // display to get screen rotation
        Display display = getWindowManager().getDefaultDisplay();
        Supplier<Integer> getDisplayRotation = display::getRotation;
        // ImageView for compass image
        ImageView compassImageView = findViewById(R.id.compassImageView);
        // TextView that will display the azimut in degrees
        TextView azimutTextView = findViewById(R.id.azimutTextView);
        CompassViewOrientationCorrector compassViewOrientationCorrector = new CompassViewOrientationCorrector(getDisplayRotation);
        CompassTextViewUpdater compassTextViewUpdater = new CompassTextViewUpdater(azimutTextView, compassViewOrientationCorrector);
        CompassImageViewUpdater compassImageViewUpdater = new CompassImageViewUpdater(compassImageView, compassViewOrientationCorrector);
        return (azimut, isDisplayUp) -> {
            compassTextViewUpdater.onNewAzimut(azimut, isDisplayUp);
            compassImageViewUpdater.onNewAzimut(azimut, isDisplayUp);
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(accelerationSensorEventListener, accelerometer, SAMPLING_PERIOD_US);
        sensorManager.registerListener(magneticSensorEventListener, magnetometer, SAMPLING_PERIOD_US);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listeners and save battery
        sensorManager.unregisterListener(accelerationSensorEventListener);
        sensorManager.unregisterListener(magneticSensorEventListener);
    }
}