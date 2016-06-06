package com.example.android.sunshine.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import static com.example.android.sunshine.app.MyWatchFace.HIGH_WATCH_FACE;
import static com.example.android.sunshine.app.MyWatchFace.LOW_WATCH_FACE;

public class WatchListenerActivity extends Activity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    //Android Wear Logic
    private static final String LOW_KEY = "LOW_KEY";
    private static final String HIGH_KEY = "HIGH_KEY";
    static final String TAG = "PIPE";

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_listener);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApiIfAvailable(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Log.d(TAG, "ACTIVITY CREATED");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connected!!");
        Wearable.DataApi.addListener(mGoogleApiClient, this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "DataItem changed!!");

        for (DataEvent event : dataEvents) {

            String highMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap().getString(HIGH_KEY);
            String lowMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap().getString(LOW_KEY);
            if (highMap != null) HIGH_WATCH_FACE = highMap;
            if (lowMap != null) LOW_WATCH_FACE = lowMap;
            Log.d(TAG, "DataItem : " + HIGH_WATCH_FACE + "  " + LOW_WATCH_FACE);


        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Suspended!!");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed! " + connectionResult.toString());
    }


}
