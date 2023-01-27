package ch.dekuen.android.compass;

import java.util.ArrayList;
import java.util.List;

public abstract class AzimutProducer {
    private final List<AzimutListener> listeners = new ArrayList<>();

    public final void addListener(AzimutListener listener) {
        listeners.add(listener);
    }

    protected final void callListeners(float azimut) {
        listeners.forEach(listener -> listener.onNewAzimut(azimut));
    }
}
