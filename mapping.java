package com.example.tuugu.positioningapp;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
/**
 ** Created by Tuguldur Batjargal on 01.04.2017.
 **/

public class mapping {

    GoogleMap map;                         // Instance of a map                                          // Instance of a map
    public GroundOverlay fleemingjenkins; // ground floor map
    public BitmapDescriptor location;


    private Context context;          // Context passed from the activity


    public mapping(GoogleMap map, Context context) {
        this.map = map;
        this.context = context;
        location = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
    }
    // Add marker to required location and move camera of the map there
    public Marker addMarker(LatLng latLng){
        Marker m = map.addMarker(new MarkerOptions().position(latLng)
                .draggable(true));
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        return m;
    }
    // Ground floor overlay to indoor Google map
    public void setupMapOverlays() {

        fleemingjenkins = map.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.fleemingjenkins)).bearing(57).position(new LatLng(55.922686, -3.172950), 94.2f).anchor(0.018f, 0.8186f)
                .transparency(0f));
    }
}


