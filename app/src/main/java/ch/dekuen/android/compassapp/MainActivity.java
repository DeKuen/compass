package ch.dekuen.android.compassapp;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ch.dekuen.android.compassapp.listener.CompassEventListener;

public class MainActivity extends AppCompatActivity {

    // for fast response, alternatively use SENSOR_DELAY_UI
    private static final int SAMPLING_PERIOD_US = SensorManager.SENSOR_DELAY_GAME;

    private final CompassEventListener compassEventListener = new CompassEventListener(this::updateAzimut);

    // device sensor manager
    private SensorManager sensorManager;

    // define the compass picture that will be use
    private ImageView compassImageView;
    private TextView azimutTextView;

    // record the angle turned of the compass picture
    private float lastAzimut = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ImageView for compass image
        compassImageView = findViewById(R.id.compassImageView);
        // TextView that will display the azimut in degrees
        azimutTextView = findViewById(R.id.azimutTextView);
        // initialize your android device sensor capabilities
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

    private void updateAzimut(float azimut)
    {
        String text = Math.round(azimut * 10f) / 10f + " °";
        azimutTextView.setText(text);
        // rotation animation - reverse turn azimut degrees
        RotateAnimation ra = new RotateAnimation(
                lastAzimut,
                -azimut,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        // set the compass animation after the end of the reservation status
        ra.setFillAfter(true);
        // set how long the animation for the compass image will take place
        ra.setDuration(210);
        // Start animation of compass image
        compassImageView.startAnimation(ra);
        lastAzimut = -azimut;
    }
}