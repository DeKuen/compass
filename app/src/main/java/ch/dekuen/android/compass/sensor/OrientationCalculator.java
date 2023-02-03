package ch.dekuen.android.compass.sensor;

import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;

import androidx.annotation.NonNull;

import ch.dekuen.android.compass.AzimutListener;

public class OrientationCalculator {
    private final AzimutListener listener;
    private final Display display;

    public OrientationCalculator(AzimutListener listener, Display display) {
        this.listener = listener;
        this.display = display;
    }

    public void calculate(float[] matrixR) {
        // orientation contains: azimut, pitch and roll
        float[] orientation = getOrientation(matrixR);

        // Pitch, angle of rotation about the x axis. The range of values is -π/2 to π/2.
        float pitch = orientation[1];
        // Roll, angle of rotation about the y axis. The range of values is -π to π.
        float roll = orientation[2];

        // todo remove
        // Log.d(getClass().getName(), "Pitch is: " + Math.toDegrees(pitch));
        // Log.d(getClass().getName(), "Roll is: " + Math.toDegrees(roll));

        // pitch has a range of only a half circle (-π/2 to π/2) and will report 0 if the phone is flat both with display facing up and facing down.
        // roll has a range of a full circle (-π to π) and will report 0 with display up and -π or π with display facing down
        boolean isDisplayUp = Math.abs(roll) <= Math.PI / 2;

        // Azimuth, angle of rotation about the -z axis - the angle between the device's y axis and the magnetic north pole. The range of values is -π to π.
        // Azimut is normally measured about the z-axis. This is fine if the phone is in portrait mode and flat on a table with display facing upwards.
        // But in portrait mode with an upright phone (pitch = -π/2) the azimut by the y-axis makes more sense.
        // Thus the coordinate system must be remapped. Remapping starts at an pitch of π/4.

        Float azimut = null;

        // quadrant 1: pitch -π/4 to π/4 and isDisplayUp = true
        if (Math.abs(pitch) <= Math.PI/4 && isDisplayUp) {
            // todo remove logging
            Log.d(getClass().getName(), "Quadrant 1");
            // already orientated correctly
            azimut = orientation[0];
        }
        // octant 3: pitch > π/4 and isDisplayUp = true
        else if (pitch > Math.PI/4 && isDisplayUp) {
            // todo remove logging
            Log.d(getClass().getName(), "Octant 3");
            azimut = getAzimutFromRemappedOrientation(matrixR, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z);
        }
        // octant 4: pitch > π/4 and isDisplayUp = false
        else if (pitch > Math.PI/4 && !isDisplayUp) {
            // todo remove logging
            Log.d(getClass().getName(), "Octant 4");
            azimut = getAzimutFromRemappedOrientation(matrixR, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z);
            Log.d(getClass().getName(), "azimut: " + Math.toDegrees(azimut));
            azimut = rotateAzimutByXAxis(azimut);

            // float azimutRotateY = rotateAzimutByYAxis(azimut);
            // Log.d(getClass().getName(), "azimutRotateY: " + Math.toDegrees(azimutRotateY));

            // azimut = getAzimutFromRemappedOrientation(matrixR, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Y);


            /*
            // invert azimut
            float azimutOrig = orientation[0];

            azimut = azimutOrig;
            Log.d(getClass().getName(), "azimut orig: " + Math.toDegrees(azimut) + " : " + azimut / Math.PI);
            double xPos = Math.cos(azimutOrig);
            double yPos = Math.sin(azimutOrig);
            Log.d(getClass().getName(), "azimut orig: cos()/sin()" + xPos + " : " + yPos);

            azimut = (float) Math.atan2(yPos, xPos);
            Log.d(getClass().getName(), "azimut orig2: " + Math.toDegrees(azimut) + " : " + azimut / Math.PI);

            azimut = (float) Math.atan2(yPos, -xPos);
            Log.d(getClass().getName(), "azimut -/+: " + Math.toDegrees(azimut) + " : " + azimut / Math.PI);

            azimut = (float) Math.atan2(-yPos, -xPos);
            Log.d(getClass().getName(), "azimut -/-: " + Math.toDegrees(azimut) + " : " + azimut / Math.PI);

            // correct
            azimut = (float) Math.atan2(-yPos, xPos);
            Log.d(getClass().getName(), "azimut +/-: " + Math.toDegrees(azimut) + " : " + azimut / Math.PI);
            */
        }
        // quadrant 3: pitch -π/4 to π/4 and isDisplayUp = false
        // todo wrong
        else if (Math.abs(pitch) <= Math.PI/4 && !isDisplayUp) {
            // todo remove logging
            Log.d(getClass().getName(), "Quadrant 3");
            azimut = rotateAzimutByYAxis(orientation[0]);
        }
        // octant 7: pitch < -π/4 and isDisplayUp = false
        // todo wrong
        else if (pitch < -Math.PI/4 && !isDisplayUp) {
            // todo remove logging
            Log.d(getClass().getName(), "Octant 7");
        }
        // octant 8: pitch < -π/4 and isDisplayUp = true
        // todo wrong
        else if (pitch < -Math.PI/4 && isDisplayUp) {
            // todo remove logging
            Log.d(getClass().getName(), "Octant 8");
        }

        if(azimut == null) {
            Log.e(getClass().getName(), "Azimut was not set, falling back to default");
            azimut = orientation[0];
        }

        // send to listener
        listener.onNewAzimut(azimut);
    }

    @NonNull
    private float rotateAzimutByXAxis(float azimut) {
        // rotate azimut by the x-axis
        // Caution: y-pos is first parameter of atan2()
        return (float) Math.atan2(Math.sin(azimut), -Math.cos(azimut));
    }

    @NonNull
    private float rotateAzimutByYAxis(float azimut) {
        // rotate azimut by the y-axis
        // Caution: y-pos is first parameter of atan2()
        return (float) Math.atan2(-Math.sin(azimut), Math.cos(azimut));
    }

    private float[] getOrientation(float[] matrix) {
        return SensorManager.getOrientation(matrix, new float[3]);
    }

    private float getAzimutFromRemappedOrientation(float[] matrixIn, int x, int y) {
        float[] matrix = remapCoordinateSystem(matrixIn, x, y);
        float[] orientation = getOrientation(matrix);
        return orientation[0];
    }

    private float[] remapCoordinateSystem(float[] matrixIn, int x, int y) {
        float[] matrixOut = new float[9];
        boolean success = SensorManager.remapCoordinateSystem(matrixIn,
                x, y,
                matrixOut);
        if(success) {
            return matrixOut;
        }
        Log.e(getClass().getName(), "Could not remap coordinate system for x/y: " + x + "/" + y);
        return matrixIn;
    }
}
