package ch.dekuen.android.compass.sensor;

import ch.dekuen.android.compass.AzimutListener;
import ch.dekuen.android.compass.AzimutProducer;

public class LowPassFilterService extends AzimutProducer implements AzimutListener {
    private static final float alpha = 0.97f;
    private float azimut;

    @Override
    public void onNewAzimut(float newAzimut) {
        azimut = alpha * azimut + (1 - alpha) * newAzimut;
        callListeners(azimut);
    }
}
