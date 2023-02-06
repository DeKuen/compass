package ch.dekuen.android.compass.view;

import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import java.util.concurrent.atomic.AtomicBoolean;

import ch.dekuen.android.compass.AzimutListener;

public class CompassImageViewUpdater implements AzimutListener {
    private final ImageView compassImageView;
    private final CompassViewOrientationCorrector compassViewOrientationCorrector;
    private final AtomicBoolean isRotating = new AtomicBoolean(false);
    private double lastDegree = 0;
    private final RotationEndListener rotationEndListener = new RotationEndListener();

    public CompassImageViewUpdater(ImageView compassImageView, CompassViewOrientationCorrector compassViewOrientationCorrector) {
        this.compassImageView = compassImageView;
        this.compassViewOrientationCorrector = compassViewOrientationCorrector;
    }

    @Override
    public void onNewAzimut(float azimutRadians, boolean isDisplayUp) {
        if(isRotating.compareAndSet(false, true)) {
            double azimutDegrees = Math.toDegrees(
                    compassViewOrientationCorrector.correctOrientation(azimutRadians, isDisplayUp)
            );
            // rotation animation - reverse turn azimutDegrees degrees
            RotateAnimation rotateAnimation = new RotateAnimation(
                    (float) lastDegree,
                    (float) -azimutDegrees,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            // store last value
            lastDegree = -azimutDegrees;
            // set the compass animation after the end of the reservation status
            rotateAnimation.setFillAfter(true);
            rotateAnimation.setAnimationListener(rotationEndListener);
            // Start animation of compass image in UI thread
            compassImageView.post(() -> compassImageView.startAnimation(rotateAnimation));
        }
    }

    private class RotationEndListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
            // ignored
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            isRotating.set(false);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // ignored
        }
    }
}