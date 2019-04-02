package com.example.tuugu.positioningapp;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.tuugu.positioningapp.R.id.map;
/**
 ** Created by Tuguldur Batjargal on 01.04.2017.
 **/
public class Training extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;   // Google Map instance
    private mapping mapping;    // Mapping instance
    private List<Marker> markerList = new ArrayList<>();    // List of map markers
    LocationManager locationManager;          //This class provides access to the system location services.
    LocationListener locationListener;       //Used for receiving notifications from the LocationManager when the location has changed
    String bestprovider;                     // The criteria for bestlocation which handles handover between indoor and outdoor
    Criteria criteria;
    DataBase myDB;                           //Accessing DataBase class with my DB


    int MY_PERMISSIONS_REQUEST_ACCESS_WIFI_STATE = 0;
    int MY_PERMISSIONS_REQUEST_CHANGE_WIFI_STATE = 1;
    int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 2;
    int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 3;


    private LatLng MyLocation;
    //This class provides the primary API for managing all aspects of Wi-Fi connectivity
    WifiManager WiFimanager;
    String Wifis[];
    WifiReceiver RecieverWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        myDB = new DataBase(this);    // opening my database
        WiFimanager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        Log.i("TAG", "got wifi manager!");


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_WIFI_STATE}, MY_PERMISSIONS_REQUEST_ACCESS_WIFI_STATE);
        }
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CHANGE_WIFI_STATE}, MY_PERMISSIONS_REQUEST_CHANGE_WIFI_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        //LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //setting bestprovider conditions or criteria
        bestprovider = locationManager.getBestProvider(getcriteria(), true);
        //LocationListener and best location handover between the system
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                setUpMapIfNeeded();
            }

            @Override
            public void onProviderDisabled(String provider) {
                //TODO
            }

            @Override
            public void onProviderEnabled(String Provider) {
                //TODO
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //TODO
            }
        };

        //Ask the user to enable/activate GPS
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Open GPS", Toast.LENGTH_LONG).show();
        }
        //Permission to use location service
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
        locationManager.requestLocationUpdates(bestprovider, 0, 0, locationListener);

    }

    //Criteria for bestlocation
    private Criteria getcriteria() {
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        return criteria;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Instantiate map and setup listeners
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
        //mMap.setOnMarkerDragListener(this);
        mMap.setPadding(0,70,0,0);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Move Google Map to King's Buildings
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.922070, -3.172315), 18.5f));


        //Checking permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);

        //calling mapping class
        mapping = new mapping(mMap, this);
        // setting ground overlays for the map
        mapping.setupMapOverlays();


    }

    // When user clicks on the map, add marker to that (current) location and scan for wifi
    @Override
    public void onMapClick(LatLng latLng) {
        // add the marker
        markerList.add(mapping.addMarker(latLng));
        MyLocation = latLng;
        scanforwifi();
    }
    // Class to receive WiFi signals whenever startScan() finishes
    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent){

            List<ScanResult> wifiScanlist = WiFimanager.getScanResults();
            WiFimanager.getScanResults();
            Wifis = new String[wifiScanlist.size()];

            String bssids = "";
            String levels = "";
            for (int i = 0; i < wifiScanlist.size(); i++){
                Wifis[i] = wifiScanlist.get(i).level+" "+wifiScanlist.get(i).BSSID;      // scan list for signal strength level and BSSID
            }

            Arrays.sort(Wifis);             // sort the list

            int storeCount = Wifis.length;           // add as many wifi signals as available to the database

            // Go through list and put relevant info into BSSID and signal strength level strings
            for(int i= 0; i < storeCount; i++) {
                String[] res = Wifis[i].split(" ");
                bssids += res[1] + ",";
                levels += res[0] + ",";
            }
            // Extract relevant substring (remove last comma character)
            bssids = bssids.substring(0, bssids.length()-1);
            levels = levels.substring(0, levels.length()-1);
            // put info into database, including latitude and longitude
            myDB.insertData(bssids, levels, MyLocation.latitude, MyLocation.longitude);
            Toast.makeText(getApplicationContext(), "Location scan finished", Toast.LENGTH_SHORT).show();  // inform the user about completed task

        }
    }
    // If user clicks on the "Show Wi-Fi Scan" button, all markers that have been saved previously
    // in the database will be showed on the screen
    public void viewData(View view){

        Cursor cursor = myDB.getAllData();
        if (cursor.getCount() == 0){
            Toast.makeText(getApplicationContext(), "No Data to Show", Toast.LENGTH_SHORT).show();
            return;
        }
        // Geting the value of latitude and longitude and adds marker at that location
        cursor.moveToFirst();
        do {
            mapping.addMarker(new LatLng(cursor.getDouble(2), cursor.getDouble(3)));
        } while (cursor.moveToNext());

    }

    public void scanforwifi(){
        WiFimanager.startScan();
        RecieverWifi = new WifiReceiver();
        registerReceiver(RecieverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public void showMessage(String title, String message){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }



    // If user clicks on marker that was added previously, ask if user wants to delete marker. If yes,
    // remove marker from map and marker list, and remove marker location info from database
    @Override
    public boolean onMarkerClick(final Marker marker) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        Log.i("TAG", "Built ALERTDIALOG");

        new AlertDialog.Builder(this)
                .setTitle("Do you want to remove this marker?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // delete marker

                        markerList.remove(marker);  // Remove marker from the marker list
                        marker.remove();            // Remove marker from the map
                        Log.i("TAG","SAIDYES");
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        Log.i("TAG","SAIDNO");
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        return false;
    }


    // Unregister listeners and wifi receiver to not consume device battery
    @Override
    protected void onPause() {
        super.onPause();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.removeUpdates(locationListener);
        unregisterReceiver(RecieverWifi);
    }
    // Register listeners and WiFi receiver on resuming activity
    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
        locationManager.requestLocationUpdates(bestprovider, 0, 0, locationListener);
        setUpMapIfNeeded();
    }
// Set up the Google Map if needed
    private void setUpMapIfNeeded() {
        if(mMap == null){

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
            mapFragment.getMapAsync(this);
        }
    }



}
