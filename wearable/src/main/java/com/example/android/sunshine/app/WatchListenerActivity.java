package com.example.android.sunshine.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import static com.example.android.sunshine.app.MyWatchFace.HIGH_WATCH_FACE;
import static com.example.android.sunshine.app.MyWatchFace.ICON_WATCH_FACE;
import static com.example.android.sunshine.app.MyWatchFace.LOW_WATCH_FACE;

public class WatchListenerActivity extends Activity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    //Android Wear Logic
    private static final String LOW_KEY = "LOW_KEY";
    private static final String HIGH_KEY = "HIGH_KEY";
    private static final String IMG_KEY = "IMG_KEY";

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
            Asset asset = DataMapItem.fromDataItem(event.getDataItem()).getDataMap().getAsset(IMG_KEY);
            if (asset != null) ICON_WATCH_FACE = loadBitmapFromAsset(asset);
            if (highMap != null) HIGH_WATCH_FACE = highMap;
            if (lowMap != null) LOW_WATCH_FACE = lowMap;
            Log.d(TAG, "DataItem : " + HIGH_WATCH_FACE + "  " + LOW_WATCH_FACE);


        }
    }

    public Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result =
                mGoogleApiClient.blockingConnect(5000, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, asset).await().getInputStream();
        mGoogleApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w(TAG, "Requested an unknown Asset.");
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
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
