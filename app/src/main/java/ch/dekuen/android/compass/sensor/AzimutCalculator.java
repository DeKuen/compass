package ch.dekuen.android.compass.sensor;

import ch.dekuen.android.compass.AzimutListener;

public class AzimutCalculator {
    private final AzimutListener listener;

    public AzimutCalculator(AzimutListener listener) {
        this.listener = listener;
    }

    public void onOrientationChanged(float[] orientation) {
        listener.onNewAzimut(getAzimut(orientation));
    }

    private float getAzimut(float[] orientation) {
        // Azimuth, angle of rotation about the -z axis.
        // Angle between the device's y axis and the magnetic north pole.
        // The range of values is -π to π.
        float azimut = orientation[0];
        // Pitch, angle of rotation about the x axis.
        // Angle between a plane parallel to the device's screen and a plane parallel to the ground.
        // The range of values is -π/2 to π/2.
        float pitch = orientation[1];

        // horizontal projection azimutFlat of azimut angle (y-axis to north) is given by:
        // cos(azimutFlat) = cos(azimut)/cos(pitch)
        // azimutFlat = acos ( cos(azimut) / cos(pitch) )
        // with azimutFlat = 0 if pitch = π
        // pitch has to be positive or * -1 and azimutFlat also * -1
        if(pitch == Math.PI / 2) {
            return 0f;
        }
        float cosAzimut = (float) Math.cos(azimut);
        float cosPitch = (float) Math.cos(pitch);
        float dividedCos = cosAzimut / cosPitch;
        if(dividedCos < -1 || dividedCos > 1) {
            return 0f;
        }
        return (float) Math.acos(dividedCos)
                *
                (azimut < 0 ? -1 : 1);
    }
}
