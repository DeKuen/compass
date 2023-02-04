package ch.dekuen.android.compass.view;

import android.widget.TextView;

import java.text.DecimalFormat;

import ch.dekuen.android.compass.AzimutListener;

public class CompassTextViewUpdater implements AzimutListener {
    static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###" + "°");
    private final TextView azimutTextView;

    public CompassTextViewUpdater(TextView azimutTextView) {
        super();
        this.azimutTextView = azimutTextView;
    }

    @Override
    public void onNewAzimut(float azimutRadians) {
        double azimutDegrees = Math.toDegrees(azimutRadians);
        String text = DECIMAL_FORMAT.format(azimutDegrees);
        azimutTextView.setText(text);
    }
}