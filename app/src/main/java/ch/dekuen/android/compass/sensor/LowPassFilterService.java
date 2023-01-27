package ch.dekuen.android.compass.sensor;

import java.util.ArrayList;
import java.util.List;

import ch.dekuen.android.compass.AzimutListener;

public class LowPassFilterService implements AzimutListener {
    private static final float alpha = 0.97f;
    private final List<AzimutListener> listeners = new ArrayList<>();
    private float azimut;

    public void addListener(AzimutListener listener) {
        listeners.add(listener);
    }

    @Override
    public void onNewAzimut(float newAzimut) {
        azimut = alpha * azimut + (1 - alpha) * newAzimut;
        listeners.forEach(listener -> listener.onNewAzimut(azimut));
    }
}
