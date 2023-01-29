package ch.dekuen.android.compass.view;

import android.widget.TextView;

import java.text.DecimalFormat;

public class CompassTextViewService extends CompassViewService {
    static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###" + "°");
    private final TextView azimutTextView;

    public CompassTextViewService(TextView azimutTextView) {
        super();
        this.azimutTextView = azimutTextView;
    }

    @Override
    public void onNewAzimut(float azimutRadians) {
        double azimutDegrees = getViewDegrees(azimutRadians);
        String text = DECIMAL_FORMAT.format(azimutDegrees);
        azimutTextView.setText(text);
    }
}