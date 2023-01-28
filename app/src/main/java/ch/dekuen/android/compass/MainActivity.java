package ch.dekuen.android.compass;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ImageView;
import android.widget.TextView;

import ch.dekuen.android.compass.view.CompassImageViewService;
import ch.dekuen.android.compass.view.CompassTextViewService;

public class MainActivity extends Activity {
    private final CustomServiceConnection connection = new CustomServiceConnection();
    private AzimutListener azimutTextViewListener;
    private AzimutListener azimutImageViewListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // ImageView for compass image
        ImageView compassImageView = findViewById(R.id.compassImageView);
        // TextView that will display the azimut in degrees
        TextView azimutTextView = findViewById(R.id.azimutTextView);
        CompassTextViewService textViewService = new CompassTextViewService(azimutTextView);
        CompassImageViewService imageViewService = new CompassImageViewService(compassImageView);
        azimutTextViewListener = azimut -> runOnUiThread(() -> textViewService.onNewAzimut(azimut));
        azimutImageViewListener = azimut -> runOnUiThread(() -> imageViewService.onNewAzimut(azimut));
        Intent intent = new Intent(this, AzimutService.class);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Bind to AzimutService
        Intent intent = new Intent(this, AzimutService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(connection);
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private class CustomServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to AzimutService, cast the IBinder and get AzimutService instance
            AzimutService.AzimutServiceBinder binder = (AzimutService.AzimutServiceBinder) service;
            AzimutService azimutService = binder.getService();
            azimutService.registerListener(azimutTextViewListener);
            azimutService.registerListener(azimutImageViewListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // ignored
        }
    }
}