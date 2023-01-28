package ch.dekuen.android.compass.view;

import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import java.util.concurrent.atomic.AtomicBoolean;

import ch.dekuen.android.compass.AzimutListener;

public class CompassImageViewService implements AzimutListener {
    private final ImageView compassImageView;
    private final AtomicBoolean isRotating = new AtomicBoolean(false);
    private double lastDegree = 0;

    public CompassImageViewService(ImageView compassImageView) {
        this.compassImageView = compassImageView;
    }

    @Override
    public void onNewAzimut(float azimut) {
        if(isRotating.compareAndSet(false, true)) {
            double azimutDegrees = Math.toDegrees(azimut);
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
            // set how long the animation for the compass image will take place
            // rotateAnimation.restrictDuration(100);
            // accelerate/decelerate animation
            // rotateAnimation.setInterpolator(new LinearInterpolator());
            rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isRotating.set(false);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            // Start animation of compass image
            compassImageView.startAnimation(rotateAnimation);
        } else {
            Log.d(getClass().getName(), "Ignoring azimut: " + azimut);
        }
    }
}