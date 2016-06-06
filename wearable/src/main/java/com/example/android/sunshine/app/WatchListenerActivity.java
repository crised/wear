package com.example.android.sunshine.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

public class WatchListenerActivity extends Activity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    //Android Wear Logic
    private static final String LOW_KEY = "LOW_KEY";
    private static final String HIGH_KEY = "HIGH_KEY";
    static final String TAG = "PIPE";


    private GoogleApiClient mGoogleApiClient;
    private int count = 0;

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
            DataItem item = event.getDataItem();
            if (item.getUri().getPath().compareTo("/count") == 0) {
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                updateHigh(dataMap.getString(HIGH_KEY));
                updateLow(dataMap.getString(LOW_KEY));

            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
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

    private void updateHigh(String s) {
        Log.d(TAG, "High: " + s);


    }

    private void updateLow(String s) {
        Log.d(TAG, "Low: " + s);


    }
}
