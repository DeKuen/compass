package ch.dekuen.android.compass.view;

import android.widget.TextView;

import java.text.DecimalFormat;

import ch.dekuen.android.compass.AzimutListener;

public class CompassTextViewService implements AzimutListener {
    static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###.# " + " °");
    private final TextView azimutTextView;

    public CompassTextViewService(TextView azimutTextView) {
        this.azimutTextView = azimutTextView;
    }

    @Override
    public void onNewAzimut(float azimut) {
        double azimutDegrees = Math.toDegrees(azimut);
        String text = DECIMAL_FORMAT.format(azimutDegrees);
        azimutTextView.setText(text);
    }
}