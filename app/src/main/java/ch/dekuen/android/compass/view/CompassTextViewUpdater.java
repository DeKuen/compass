package ch.dekuen.android.compass.view;

import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Objects;

import ch.dekuen.android.compass.AzimutListener;

public class CompassTextViewUpdater implements AzimutListener {
    static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###" + "°");
    private final TextView azimutTextView;
    private final CompassViewOrientationCorrector compassViewOrientationCorrector;

    public CompassTextViewUpdater(TextView azimutTextView, CompassViewOrientationCorrector compassViewOrientationCorrector) {
        this.azimutTextView = Objects.requireNonNull(azimutTextView);
        this.compassViewOrientationCorrector = compassViewOrientationCorrector;
    }

    @Override
    public void onNewAzimut(float azimutRadians, boolean isDisplayUp) {
        double azimutDegrees = Math.toDegrees(
                compassViewOrientationCorrector.correctOrientation(azimutRadians, isDisplayUp)
        );
        String text = DECIMAL_FORMAT.format(azimutDegrees);
        // Update text in UI thread
        azimutTextView.post(() -> azimutTextView.setText(text));
    }
}