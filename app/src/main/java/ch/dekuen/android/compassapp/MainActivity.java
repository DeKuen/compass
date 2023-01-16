package ch.dekuen.android.compassapp;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ch.dekuen.android.compassapp.listener.CompassEventListener;
import ch.dekuen.android.compassapp.service.AzimutService;
import ch.dekuen.android.compassapp.service.CompassViewService;

public class MainActivity extends AppCompatActivity {

    // SENSOR_DELAY_GAME for fast response, alternatively use SENSOR_DELAY_UI or SENSOR_DELAY_NORMAL
    private static final int SAMPLING_PERIOD_US = SensorManager.SENSOR_DELAY_NORMAL;

    private CompassEventListener compassEventListener;

    // device sensor manager
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ImageView for compass image
        ImageView compassImageView = findViewById(R.id.compassImageView);
        // TextView that will display the azimut in degrees
        TextView azimutTextView = findViewById(R.id.azimutTextView);
        CompassViewService compassViewService = new CompassViewService(compassImageView, azimutTextView);
        compassEventListener = new CompassEventListener(new AzimutService(), compassViewService::updateCompass);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        sensorManager.unregisterListener(compassEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(compassEventListener, accelerometer, SAMPLING_PERIOD_US);
        sensorManager.registerListener(compassEventListener, magnetometer, SAMPLING_PERIOD_US);
    }
}