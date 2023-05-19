package com.slava.localization;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;

import java.util.HashMap;
import java.util.List;

public class ScannedAP extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;

    WifiManager mWifiManager;
    IntentFilter mIntentFilter;
    String mApStr;
    boolean NOT_EMPTY = false;
    HashMap<String, Float> sig_str;
    Button yes_button;
    Button no_button;

    private void setTextView(String str) {
        TextView tv = (TextView)
                findViewById(R.id.textview);
        tv.setMovementMethod(new
                ScrollingMovementMethod());
        tv.setText(str);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                List<ScanResult> scanResults = mWifiManager.getScanResults();
                mApStr = "";
                sig_str = new HashMap<>();
                if (scanResults.size() > 0) NOT_EMPTY = true;
                for (ScanResult result : scanResults) {
                    mApStr = mApStr + result.SSID + "; ";
                    mApStr = mApStr + result.BSSID + "; ";
                    mApStr = mApStr + result.capabilities + "; ";
                    mApStr = mApStr + result.frequency + " MHz;";
                    mApStr = mApStr + result.level + " dBm\n\n";
                    sig_str.put(result.BSSID, (float) result.level);
                }
// Update UI to show all this information.
                setTextView(mApStr);
            }
        }
    };

    private void finish_activity(boolean flag) {
        Intent intent = new Intent();
        intent.putExtra("result", flag);
        intent.putExtra("not_empty", NOT_EMPTY);
        intent.putExtra("data", sig_str);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_ap);

        yes_button = findViewById(R.id.yes_button_save);
        no_button = findViewById(R.id.no_button_save);

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
        boolean scanStarted = mWifiManager.startScan();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish_activity(true);
            }
        });

        no_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                finish_activity(false);
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(mReceiver,
                mIntentFilter);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
}