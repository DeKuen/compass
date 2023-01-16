package ch.dekuen.android.compassapp.service;

import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class CompassViewService {
    private final ImageView compassImageView;
    private final TextView azimutTextView;
    private int lastDegree = 0;

    public CompassViewService(ImageView compassImageView, TextView azimutTextView) {
        this.compassImageView = compassImageView;
        this.azimutTextView = azimutTextView;
    }

    public void updateCompass(float azimutExact)
    {
        int azimut = Math.round(azimutExact);
        if(azimut == -lastDegree)
        {
            return;
        }
        String text = azimut + " °";
        azimutTextView.setText(text);
        // rotation animation - reverse turn azimut degrees
        RotateAnimation ra = new RotateAnimation(
                lastDegree,
                -azimut,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        // set the compass animation after the end of the reservation status
        ra.setFillAfter(true);
        // set how long the animation for the compass image will take place
        ra.setDuration(210);
        // Start animation of compass image
        compassImageView.startAnimation(ra);
        lastDegree = -azimut;
    }
}