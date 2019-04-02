package com.example.tuugu.positioningapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

/**
 ** Created by Tuguldur Batjargal on 01.04.2017.
 **/

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    // To go to Training Activity after clicking on the OnClickTraining button
    public void OnClickTraining(View V) {

        Intent intent = new Intent(this, Training.class);
        startActivity(intent);

    }
    // To go to Positioning Activity after clicking on the OnClickPositioning button
    public void OnClickPositioning(View V) {

        Intent intent = new Intent(this, Positioning.class);
        startActivity(intent);

    }
    // To go to Extra Activity after clicking on the OnClickExtra button
    public void OnClickExtra(View V) {

        Intent intent = new Intent(this, Extra.class);
        startActivity(intent);

    }

    // The foreground lifetime of an activity happens between a call to onResume() until a corresponding call to onPause().
    // During this time the activity is in front of all other activities and interacting with the user
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
