package ch.dekuen.android.compassapp.service;

import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class CompassViewService {

    // define the compass picture that will be use
    private final ImageView compassImageView;
    private final TextView azimutTextView;

    // record the angle turned of the compass picture
    private float lastAzimut = 0f;

    public CompassViewService(ImageView compassImageView, TextView azimutTextView) {
        this.compassImageView = compassImageView;
        this.azimutTextView = azimutTextView;
    }

    public void updateCompass(float azimut)
    {
        String text = Math.round(azimut * 10f) / 10f + " °";
        azimutTextView.setText(text);
        // rotation animation - reverse turn azimut degrees
        RotateAnimation ra = new RotateAnimation(
                lastAzimut,
                -azimut,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        // set the compass animation after the end of the reservation status
        ra.setFillAfter(true);
        // set how long the animation for the compass image will take place
        ra.setDuration(210);
        // Start animation of compass image
        compassImageView.startAnimation(ra);
        lastAzimut = -azimut;
    }
}