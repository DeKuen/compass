package ch.dekuen.android.compass.service;

import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

public class CompassViewService {
    static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###.# " + " °");
    private final ImageView compassImageView;
    private final TextView azimutTextView;
    private double lastDegree = 0;

    public CompassViewService(ImageView compassImageView, TextView azimutTextView) {
        this.compassImageView = compassImageView;
        this.azimutTextView = azimutTextView;
    }

    public void updateCompass(float azimutExact)
    {
        double azimutDegrees = Math.toDegrees(azimutExact);
        String text = DECIMAL_FORMAT.format(azimutDegrees);
        azimutTextView.setText(text);
        // rotation animation - reverse turn azimutDegrees degrees
        RotateAnimation rotateAnimation = new RotateAnimation(
                (float) lastDegree,
                (float) -azimutDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        // set the compass animation after the end of the reservation status
        rotateAnimation.setFillAfter(true);
        // set how long the animation for the compass image will take place
        rotateAnimation.setDuration(210);
        // Start animation of compass image
        compassImageView.startAnimation(rotateAnimation);
        lastDegree = -azimutDegrees;
    }
}