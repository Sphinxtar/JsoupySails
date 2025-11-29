package com.mrk.jsoupysails;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiNetworkSpecifier;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class NetworkRequester {
    private static final String TAG = "NetworkRequester";
    public Context context;
    public Context baseContext;
    // ConnectivityManager connectivityManager;

    public Network waitForNetwork(Context context, int timeoutMs) {
        // Use a CountDownLatch with a count of 1
        final CountDownLatch latch = new CountDownLatch(1);
        final Network[] resultNetwork = {null};
        this.context = context;
        this.baseContext = ((ContextWrapper) context).getBaseContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Build the WifiNetworkSpecifier
        WifiNetworkSpecifier specifier = new WifiNetworkSpecifier.Builder()
                .setSsid(context.getString(R.string.ssid))
                .setWpa2Passphrase(context.getString(R.string.pass))
                .build();

        // Define a NetworkCallback
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                Log.d(TAG, "Network available: " + network);
                // Store the network and release the latch
                resultNetwork[0] = network;
                connectivityManager.bindProcessToNetwork(network);
                latch.countDown();
                String msg = (context.getString(R.string.connect) + context.getString(R.string.ssid));
                Toast toast = Toast.makeText(baseContext, msg, Toast.LENGTH_LONG);
                toast.show();
            }

            @Override
            public void onUnavailable() {
                super.onUnavailable();
                Log.d(TAG, "Network unavailable within timeout.");
                // Release the latch if no network is found
                latch.countDown();
                String msg = (context.getString(R.string.netfail) + context.getString(R.string.connect) + context.getString(R.string.ssid));
                Toast toast = Toast.makeText(baseContext, msg, Toast.LENGTH_LONG);
                toast.show();
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                Log.d(TAG, "Network lost: " + network);
                String msg = (context.getString(R.string.netlost) + context.getString(R.string.ssid));
                Toast toast = Toast.makeText(baseContext, msg, Toast.LENGTH_LONG);
                toast.show();
            }
        };

        // Create the NetworkRequest
        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                // If you intend to use this network for internet access, you might need to adjust capabilities.
                // For local-only networks (e.g., IoT), NET_CAPABILITY_INTERNET is often removed.
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .setNetworkSpecifier(specifier)
                .build();

        // Request the network
        connectivityManager.requestNetwork(request, networkCallback, timeoutMs);

        try {
            // Wait for the latch to be released, with a timeout
            latch.await(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, "Network request interrupted", e);
            Thread.currentThread().interrupt();
        } finally {
            // Unregister the callback to prevent leaks
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }

        return resultNetwork[0];
    }
}