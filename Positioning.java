package com.example.tuugu.positioningapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.wifi.ScanResult;import android.net.wifi.WifiManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 ** Created by Tuguldur Batjargal on 01.04.2017.
 **/

public class Positioning extends FragmentActivity implements OnMapReadyCallback  {

    private GoogleMap mMap;
    private mapping mapping;                                 // Mapping instance

    //  private LocationUpdater mLocationUpdater;
    private Button selectedFloorButton;
    private EditText locationEditText;
    //  private WifiCollector mWifiCollector;
    private Button showInfoDialogButton;

    private List<Marker> markerList = new ArrayList<>();
    DataBase myDb;


    int rowCount;
    String[] BSSIDs;
    String[] signals;
    double[] lats;
    double[] lngs;

    WifiManager wifiManager;
    String wifis[];
    WifiReceiver receiverWifi;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positioning);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        myDb = new DataBase(this);    // open database
        readData();                    // Read from the database data
        receiverWifi = new WifiReceiver();          // Wifi reciever
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);



    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Instantiate map and setup listeners
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setPadding(0, 70, 0, 0);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        // Move Google Map to King's Buildings
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.922070, -3.172315), 18.5f));
        //Checking permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //calling mapping class
        mapping = new mapping(mMap, this);
        // setup ground overlays for the map
        mapping.setupMapOverlays();

    }

    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent){
            List<ScanResult> wifiScanlist = wifiManager.getScanResults();
            wifis = new String[wifiScanlist.size()];


            for (int i = 0; i < wifiScanlist.size(); i++){
                wifis[i] = wifiScanlist.get(i).level+" "+wifiScanlist.get(i).BSSID;         // scan list for signal level and BSSID
            }

            Arrays.sort(wifis);             // sort the list


            int k = 4;                   // Number (+1) of WiFi signals to be compared against database info
            // To avoid crash if number of signals at current location is less than 4
            if (wifiScanlist.size() < 4) {
                k = wifiScanlist.size();
            }
            String[] bssids = new String[k];
            int[] levels = new int[k];
            // Go through list and put relevant info into bssids and signal level strings
            for(int i= 0; i < k; i++) {
                String[] res = wifis[i].split(" ");
                bssids[i] = res[1];
                levels[i] = Integer.parseInt(res[0]);
            }

            LatLng currLocation = processData(bssids, levels);

            Log.e("currLocation", currLocation.latitude + " " + currLocation.longitude);

            // Add marker of current location if possible to determine the current location
            markerList.add(mapping.addMarker(currLocation, mapping.location));

        }
    }



    public void scanforwifi(){
        wifiManager.startScan();
    }

    public void FindLocation(View view){
        scanforwifi();
    }
    // Read data from database into arrays
    private void readData() {
        Cursor cursor = myDb.getAllData();

        rowCount = cursor.getCount();
        if (rowCount == 0) {
            Toast.makeText(this, "No data in the database", Toast.LENGTH_LONG).show();
            finish(); // go to activity where you came from
        }

        BSSIDs = new String[rowCount];
        signals = new String[rowCount];
        lats = new double[rowCount];
        lngs = new double[rowCount];

        cursor.moveToFirst();
        int i = 0;
        do {
            BSSIDs[i] = cursor.getString(0);
            signals[i] = cursor.getString(1);
            lats[i] = cursor.getDouble(2);
            lngs[i] = cursor.getDouble(3);
            i++;
        } while (cursor.moveToNext());
    }
    // Compare database information (in arrays) with current location scan
    private LatLng processData (String[] testBssids, int[] testSignals){
        // positioning stage stage
        double minSum = Double.MAX_VALUE;
        double latitude = 0;
        double longitude = 0;
        // Processing data to take signal stenght levels and BSSIDs from database
        for (int j = 0; j < rowCount; j++){
            String[] trainBssids = BSSIDs[j].split(",");
            String[] splitSignals = signals[j].split(",");
            int signalsCount = splitSignals.length;
            // Need to parse signals as integers
            int[] trainSignals = new int[signalsCount];
            for (int k = 0; k < signalsCount; k++){
                trainSignals[k] = Integer.parseInt(splitSignals[k]);
            }

            int testCount = testBssids.length;
            int count = 0;      //counts how many test signals were used
            double sum = 0;
            // calculate euclidian distance by comparing each test location to current location signal strength
            for (int k = 0; k < testCount; k++){
                for (int m = 0; m < signalsCount; m++) {
                    if (testBssids[k].equals(trainBssids[m])) {
                        sum += Math.pow(testSignals[k] - trainSignals[k], 2);
                        count++;
                    }
                }
            }

            Log.e("comapre counts", count + " <> " + testCount);
            // Using Euclidian distance formula from report and study
            sum = Math.sqrt(sum);
            // check to update lowest minsum and all testcounts were used in the euclidian calculation
            if (minSum > sum && count == testCount){
                minSum = sum;
                latitude = lats[j];
                longitude = lngs[j];

            }
        }
        Toast.makeText(getApplicationContext(), "Smallest Euclidian " + minSum, Toast.LENGTH_SHORT).show();
        return new LatLng(latitude, longitude);
    }
    // Register listeners and WiFi receiver on resuming activity
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }
    // Unregister listeners and wifi receiver to not consume device battery
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiverWifi);
    }
}
