package ch.dekuen.android.compass;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ch.dekuen.android.compass.sensor.AzimutCalculator;
import ch.dekuen.android.compass.sensor.CompassSensorEventListener;
import ch.dekuen.android.compass.view.CompassImageViewUpdater;
import ch.dekuen.android.compass.view.CompassTextViewUpdater;
import ch.dekuen.android.compass.view.CompassViewOrientationCorrector;

public class MainActivity extends Activity {

    private final List<SensorEventListener> sensorEventListeners = new ArrayList<>();
    private final List<HandlerThread> handlerThreads = new ArrayList<>();
    private AzimutCalculator azimutCalculator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AzimutListener azimutListener = getAzimutListener();
        azimutCalculator = new AzimutCalculator(azimutListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        HandlerThread calculatorHandlerThread = startBackgroundHandlerThread("calculatorThread");
        Looper calculatorLooper = calculatorHandlerThread.getLooper();

        registerListener(Sensor.TYPE_ACCELEROMETER, "accelerationSensorThread", calculatorLooper, azimutCalculator::onAccelerationSensorChanged);

        registerListener(Sensor.TYPE_MAGNETIC_FIELD, "magneticSensorThread", calculatorLooper, azimutCalculator::onMagneticSensorChanged);
    }

    private void registerListener(int sensorType, String listenerThreadName, Looper looper, Consumer<float[]> consumer) {
        Handler handler = new Handler(looper);
        CompassSensorEventListener listener = new CompassSensorEventListener(handler, consumer, sensorType, AppConstants.LOW_PASS_FILTER_ALPHA);

        HandlerThread listenerHandlerThread = startBackgroundHandlerThread(listenerThreadName);
        Handler listenerHandler = new Handler(listenerHandlerThread.getLooper());

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(sensorType);
        sensorManager.registerListener(listener, sensor, AppConstants.SAMPLING_PERIOD_US, listenerHandler);

        sensorEventListeners.add(listener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listeners and save battery
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorEventListeners.forEach(sensorManager::unregisterListener);
        sensorEventListeners.clear();
        handlerThreads.forEach(HandlerThread::quitSafely);
        handlerThreads.clear();
    }

    private HandlerThread startBackgroundHandlerThread(String threadName) {
        HandlerThread handlerThread = new HandlerThread(threadName, Process.THREAD_PRIORITY_LESS_FAVORABLE);
        // seems to be necessary to set priority by setter
        handlerThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        handlerThreads.add(handlerThread);
        return handlerThread;
    }

    private AzimutListener getAzimutListener() {
        // display to get screen rotation
        Display display = getWindowManager().getDefaultDisplay();
        CompassViewOrientationCorrector compassViewOrientationCorrector = new CompassViewOrientationCorrector(display::getRotation);
        // TextView that will display the azimut in degrees
        TextView azimutTextView = findViewById(R.id.azimutTextView);
        CompassTextViewUpdater compassTextViewUpdater = new CompassTextViewUpdater(azimutTextView, compassViewOrientationCorrector);
        // ImageView for compass image
        ImageView compassImageView = findViewById(R.id.compassImageView);
        CompassImageViewUpdater compassImageViewUpdater = new CompassImageViewUpdater(compassImageView, compassViewOrientationCorrector);
        return (azimut, isDisplayUp) -> {
            compassTextViewUpdater.onNewAzimut(azimut, isDisplayUp);
            compassImageViewUpdater.onNewAzimut(azimut, isDisplayUp);
        };
    }
}