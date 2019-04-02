package com.example.tuugu.positioningapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;

public class Compass extends AppCompatActivity implements SensorEventListener {

    private ImageView compass;

    TextView tvDirection, mag_h, acc_a;
    // getting access to sensors used
    private SensorManager mSensorManager;

    private float x, y, z;
    private double h;


    //represent accelerometer sensor
    private Sensor AccSensor;
    //represent magnetic sensor
    private Sensor MagSensor;

    float[] accelerometerValues = new float[3];
    float[] magneticFieldValues = new float[3];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        initialization();

        calculateOrientation();
    }

    public void calculateOrientation() {
        float[] values = new float[3];

        float[] R = new float[9];
        float[] I = new float[9];


        SensorManager.getRotationMatrix(R, I, accelerometerValues, magneticFieldValues);

        SensorManager.getOrientation(R, values);

        float degree = (float) (Math.toDegrees(values[0]) + 360) % 360;


        int range = (int) (degree / (360f / 16f));
        //declaring the string dirTxt string
        String dirTxt = "";
        //calculating each degree ranges for the direction of compass
        if (range == 15 || range == 0)
            dirTxt = "N";
        if (range == 1 || range == 2)
            dirTxt = "NE";
        if (range == 3 || range == 4)
            dirTxt = "E";
        if (range == 5 || range == 6)
            dirTxt = "SE";
        if (range == 7 || range == 8)
            dirTxt = "S";
        if (range == 9 || range == 10)
            dirTxt = "SW";
        if (range == 11 || range == 12)
            dirTxt = "W";
        if (range == 13 || range == 14)
            dirTxt = "NW";
        //declaring the text in the tvDirection which will be showed in the main acitivity
        tvDirection.setText("Direction: " + ((int) degree) + ((char) 176) + " "
                + dirTxt); // char 176 ) = degrees ...
        //rotating picture called compass along the degrees
        compass.setRotation((float) -degree);
    }


    private void initialization() {


        tvDirection = (TextView) findViewById(R.id.tvDirection);
        mag_h = (TextView) findViewById(R.id.magnetic);
        acc_a = (TextView) findViewById(R.id.acc);
        compass = (ImageView) findViewById(R.id.imageView);

        //getting an instance of SensorManager for accessing sensors used
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // determine a default sensor type for accelerometer and magnetic field
        AccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        MagSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        //disabling the sensors when the app is paused
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, AccSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, MagSensor, SensorManager.SENSOR_DELAY_NORMAL);
        // registering the sensors when the app is retureted to the acitivity
    }



    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        //reading the sensor event values
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];
        //calculating the total magnetic field
        h = Math.sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]);


        // setting tha sensor event values into the correct sensor values
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues[0] = x;
            accelerometerValues[1] = y;
            accelerometerValues[2] = z;
            acc_a.setText("Linear Acceleartion:" + x + "m/s^2");
            // showing the linear accelerations along the x axis in m/s^2
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticFieldValues[0] = x;
            magneticFieldValues[1] = y;
            magneticFieldValues[2] = z;
            mag_h.setText("Magnetic Field: " + Math.round(h) + " uT");
            //showing the magnetic field in uT
        }
        calculateOrientation();
        //recalling the calculateOrientation for new orientation
    }


}
