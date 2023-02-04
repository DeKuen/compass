package ch.dekuen.android.compass.sensor;

import android.hardware.SensorManager;

import ch.dekuen.android.compass.AzimutListener;

public class RotationMatrixToAzimutCalculator {
    private final AzimutListener listener;

    public RotationMatrixToAzimutCalculator(AzimutListener listener) {
        this.listener = listener;
    }

    public void calculate(float[] matrixR) {
        float azimut = getAzimut(matrixR);
        listener.onNewAzimut(azimut);
    }

    private float getAzimut(float[] matrixR) {
        float[] orientation = new float[3];
        // orientation contains: azimut, pitch and roll
        SensorManager.getOrientation(matrixR, orientation);

        // Azimuth, angle of rotation about the -z axis.
        // Angle between the device's y axis and the magnetic north pole.
        // The range of values is -π to π.
        float azimut = orientation[0];
        // Pitch, angle of rotation about the x axis.
        // Angle between a plane parallel to the device's screen and a plane parallel to the ground.
        // The range of values is -π/2 to π/2.
        float pitch = orientation[1];

        // pitch is near horizontal and azimut near 0 or +/ π ==> use y-axis azimut
        if(Math.abs(pitch) <= Math.PI / 4 && Math.abs(azimut) <= Math.PI / 8) {
            return azimut;
        }

        // horizontal projection azimutFlat of azimut angle (y-axis to north) is given by:
        // cos(azimutFlat) = cos(azimut)/cos(pitch)
        // azimutFlat = acos ( cos(azimut) / cos(pitch) )
        // with azimutFlat = 0 if pitch = π/2
        // correct by *1 or *-1
        if(pitch == Math.PI / 2) {
            return 0f;
        }
        float cosAzimut = (float) Math.cos(azimut);
        float cosPitch = (float) Math.cos(pitch);
        float dividedCos = cosAzimut / cosPitch;
        if(dividedCos < -1) {
            return (float) Math.PI;
        } else if (dividedCos > 1) {
            return 0;
        }
        return (float) Math.acos(dividedCos)
                *
                (azimut < 0 ? -1 : 1);
    }
}
