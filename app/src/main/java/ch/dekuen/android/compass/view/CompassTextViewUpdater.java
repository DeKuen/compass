package ch.dekuen.android.compass.view;

import android.util.Log;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Locale;

import ch.dekuen.android.compass.AzimutListener;

public class CompassTextViewUpdater implements AzimutListener {
    static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###" + "°");
    private final TextView azimutTextView;
    private final CompassViewOrientationCorrector compassViewOrientationCorrector;

    public CompassTextViewUpdater(TextView azimutTextView, CompassViewOrientationCorrector compassViewOrientationCorrector) {
        this.azimutTextView = azimutTextView;
        this.compassViewOrientationCorrector = compassViewOrientationCorrector;
    }

    @Override
    public void onNewAzimut(float azimutRadians, boolean isDisplayUp) {


        Thread thread = Thread.currentThread();
        String s = String.format(Locale.getDefault(),
                "%s : uses Thread name=%s, id=%d, priority=%d",
                this,
                thread.getName(),
                thread.getId(),
                thread.getPriority()
        );
        Log.d(getClass().getName(), s);

        double azimutDegrees = Math.toDegrees(
                compassViewOrientationCorrector.correctOrientation(azimutRadians, isDisplayUp)
        );
        String text = DECIMAL_FORMAT.format(azimutDegrees);
        // Update text in UI thread
        azimutTextView.post(() -> azimutTextView.setText(text));
    }
}