package ch.dekuen.android.compass.view;

import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import ch.dekuen.android.compass.AzimutListener;

public class CompassImageViewService implements AzimutListener {
    private final ImageView compassImageView;
    private double lastDegree = 0;

    public CompassImageViewService(ImageView compassImageView) {
        this.compassImageView = compassImageView;
    }

    @Override
    public void onNewAzimut(float azimut) {
        double azimutDegrees = Math.toDegrees(azimut);
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