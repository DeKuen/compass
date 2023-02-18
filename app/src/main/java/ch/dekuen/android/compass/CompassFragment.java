package ch.dekuen.android.compass;

import static android.content.Context.SENSOR_SERVICE;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ch.dekuen.android.compass.databinding.FragmentCompassBinding;
import ch.dekuen.android.compass.sensor.AzimutCalculator;
import ch.dekuen.android.compass.sensor.CompassSensorEventListener;
import ch.dekuen.android.compass.view.CompassImageViewUpdater;
import ch.dekuen.android.compass.view.CompassTextViewUpdater;
import ch.dekuen.android.compass.view.CompassViewOrientationCorrector;

public class CompassFragment extends Fragment {
    private FragmentCompassBinding binding;
    private final List<SensorEventListener> sensorEventListeners = new ArrayList<>();
    private final List<HandlerThread> handlerThreads = new ArrayList<>();
    private Activity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCompassBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        activity = requireActivity();
        AzimutListener azimutListener = getAzimutListener();
        AzimutCalculator azimutCalculator = new AzimutCalculator(azimutListener);

        HandlerThread calculatorHandlerThread = startBackgroundHandlerThread("calculatorThread");
        Looper calculatorLooper = calculatorHandlerThread.getLooper();

        registerListener(Sensor.TYPE_ACCELEROMETER, "accelerationSensorThread", calculatorLooper, azimutCalculator::onAccelerationSensorChanged);
        registerListener(Sensor.TYPE_MAGNETIC_FIELD, "magneticSensorThread", calculatorLooper, azimutCalculator::onMagneticSensorChanged);
    }

    @Override
    public void onPause() {
        super.onPause();
        // to stop the listeners and save battery
        SensorManager sensorManager = (SensorManager) activity.getSystemService(SENSOR_SERVICE);
        sensorEventListeners.forEach(sensorManager::unregisterListener);
        sensorEventListeners.clear();
        handlerThreads.forEach(HandlerThread::quitSafely);
        handlerThreads.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private AzimutListener getAzimutListener() {
        // display to get screen rotation
        Display display = activity.getWindowManager().getDefaultDisplay();
        CompassViewOrientationCorrector compassViewOrientationCorrector = new CompassViewOrientationCorrector(display::getRotation);
        // TextView that will display the azimut in degrees
        TextView azimutTextView = activity.findViewById(R.id.azimutTextView);
        CompassTextViewUpdater compassTextViewUpdater = new CompassTextViewUpdater(azimutTextView, compassViewOrientationCorrector);
        // ImageView for compass image
        ImageView compassImageView = activity.findViewById(R.id.compassImageView);
        CompassImageViewUpdater compassImageViewUpdater = new CompassImageViewUpdater(compassImageView, compassViewOrientationCorrector);
        return (azimut, isDisplayUp) -> {
            compassTextViewUpdater.onNewAzimut(azimut, isDisplayUp);
            compassImageViewUpdater.onNewAzimut(azimut, isDisplayUp);
        };
    }

    private void registerListener(int sensorType, String listenerThreadName, Looper looper, Consumer<float[]> consumer) {
        Handler handler = new Handler(looper);
        CompassSensorEventListener listener = new CompassSensorEventListener(handler, consumer, sensorType, AppConstants.LOW_PASS_FILTER_ALPHA);

        HandlerThread listenerHandlerThread = startBackgroundHandlerThread(listenerThreadName);
        Handler listenerHandler = new Handler(listenerHandlerThread.getLooper());

        SensorManager sensorManager = (SensorManager) activity.getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(sensorType);
        sensorManager.registerListener(listener, sensor, AppConstants.SAMPLING_PERIOD_US, listenerHandler);

        sensorEventListeners.add(listener);
    }

    private HandlerThread startBackgroundHandlerThread(String threadName) {
        HandlerThread handlerThread = new HandlerThread(threadName, Process.THREAD_PRIORITY_LESS_FAVORABLE);
        // seems to be necessary to set priority by setter
        handlerThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        handlerThreads.add(handlerThread);
        return handlerThread;
    }
}
