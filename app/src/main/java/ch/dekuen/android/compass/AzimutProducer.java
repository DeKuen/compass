package ch.dekuen.android.compass;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AzimutProducer {
    private final List<AzimutListener> listeners = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();


    public final void addListener(AzimutListener listener) {
        listeners.add(listener);
    }

    protected final void callListeners(float azimut) {
        listeners.parallelStream()
                .forEach(listener -> executor.submit(() -> listener.onNewAzimut(azimut)));
    }
}
