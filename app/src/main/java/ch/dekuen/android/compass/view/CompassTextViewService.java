package ch.dekuen.android.compass.view;

import android.view.Display;
import android.widget.TextView;

import java.text.DecimalFormat;

public class CompassTextViewService extends CompassViewService {
    static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###" + "°");
    private final TextView azimutTextView;

    public CompassTextViewService(Display display, TextView azimutTextView) {
        super(display);
        this.azimutTextView = azimutTextView;
    }

    @Override
    public void onNewAzimut(float azimut, boolean isPhoneFacingUp) {
        double azimutDegrees = getViewDegrees(azimut, isPhoneFacingUp);
        String text = DECIMAL_FORMAT.format(azimutDegrees);
        azimutTextView.setText(text);
    }
}