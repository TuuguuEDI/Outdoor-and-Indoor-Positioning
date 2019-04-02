package com.example.tuugu.positioningapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
/**
 ** Created by Tuguldur Batjargal on 01.04.2017.
 **/
public class Battery extends AppCompatActivity {

// Initiating filters and text
    public TextView TV;
    private IntentFilter ifilter;
// Battery Broadcast listetener for changing variables
    private BroadcastReceiver mBatInfoReciever = new BroadcastReceiver()
    {


        //battery level
        public int BatteryL;
        //battery voltage
        public int BatteryV;
        //battery temperature
        public double BatteryT;
        //battery technology
        public String BatteryTe;
        //battery status
        public String BatteryStatus;
        //battery health
        public String BatteryHealth;
        //battery plugged
        public String BatteryPlugged;





        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // Checking battery variables
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                BatteryL = intent.getIntExtra("level", 0);
                BatteryV = intent.getIntExtra("voltage", 0);
                BatteryT = intent.getIntExtra("temperature", 0);
                BatteryTe = intent.getStringExtra("technology");
                // Checking battery status
                switch (intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN)) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        BatteryStatus = "Charging";
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        BatteryStatus = "Disharging";
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        BatteryStatus = "Not Charging";
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        BatteryStatus = "Fully Charged";
                        break;
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        BatteryStatus = "Unknown Status";
                        break;
                }
                // Checking battery health status
                switch (intent.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN)) {
                    case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                        BatteryHealth = "Unknown Status";
                        break;
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        BatteryHealth = "Good Status";
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        BatteryHealth = "Dead Status";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        BatteryHealth = "Over Voltage";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        BatteryHealth = "Overheat";
                        break;
                }
                // Checking battery plugged status
                switch (intent.getIntExtra("plugged", 0)) {
                    case BatteryManager.BATTERY_PLUGGED_AC:
                        BatteryPlugged = "Plugged to AC";
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        BatteryPlugged = "Plugged to USB";
                        break;
                    case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                        BatteryPlugged = "Plugged to Wireless";
                        break;
                    default:
                        BatteryPlugged = "------";
                }
                //Printing out the results
                TV.setText("Battery Level:  " +BatteryL + "%" + "\n" + "\n" +
                        "Battery Status:  " + BatteryStatus + "\n" + "\n"  +
                        "Battery Plugged:  " + BatteryPlugged + "\n" +  "\n" +
                        "Battery Health:    " +BatteryHealth  +  "\n"  +  "\n" +
                        "Battery Voltage:    " + (BatteryV/1000) + "V" + "\n"  + "\n" +
                        "Battery Temperature: "  + (BatteryT*0.1)  + "C" + "\n" + "\n" +
                        "Battery Technology:   "  +BatteryTe);
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);
    //Passing out the variables to content.xml
        TV = (TextView)findViewById(R.id.TV);

        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        registerReceiver(mBatInfoReciever, ifilter);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
