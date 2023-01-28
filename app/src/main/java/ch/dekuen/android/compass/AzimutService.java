package ch.dekuen.android.compass;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

import ch.dekuen.android.compass.sensor.CompassSensorEventListener;

public class AzimutService extends Service {
    // SENSOR_DELAY_GAME for fast response, alternatively use SENSOR_DELAY_UI or SENSOR_DELAY_NORMAL
    private static final int SAMPLING_PERIOD_US = SensorManager.SENSOR_DELAY_GAME;
    // Binder given to clients
    private final IBinder binder = new AzimutServiceBinder();
    // device sensor manager
    private SensorManager sensorManager;

    private final CompassSensorEventListener compassSensorEventListener = new CompassSensorEventListener();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    class AzimutServiceBinder extends Binder {
        AzimutService getService() {
            // Return this instance of AzimutService so clients can call public methods
            return AzimutService.this;
        }
    }

    public final void registerListener(AzimutListener listener) {
        compassSensorEventListener.registerListener(listener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        return result;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(compassSensorEventListener, accelerometer, SAMPLING_PERIOD_US);
        sensorManager.registerListener(compassSensorEventListener, magnetometer, SAMPLING_PERIOD_US);
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        boolean result = super.onUnbind(intent);
        // to stop the listener and save battery
        sensorManager.unregisterListener(compassSensorEventListener);
        return result;
    }
}
