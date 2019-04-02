package com.example.tuugu.positioningapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 ** Created by Tuguldur Batjargal on 01.04.2017.
 **/
public class Extra extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra);




    }
// Info and Guidelines of using this app
    public void OnClickInfo(View view) {
        Toast.makeText(this, "Android App info: \n" +
                "Created by Tuguldur Batjargal \n" +
                "The University of Edinburgh \n" +
                "Embedded Mobile and Wireless Systems 5 \n" +
                "The application uses GPS, Wifi and other sensors to detect accurtate indoor and outdoor location on the map \n" +
                "Training Phase: Trains the application to improve positioning accuracy by adding data to data base \n" +
                "Positiong Phase: Determines users current location as accurately as possible using provided data from data base", Toast.LENGTH_LONG).show();

    }

    // To go to Compass Activity after clicking on the OnClickCompass button
    public void OnClickCompass(View V) {

        Intent intent = new Intent(this, Compass.class);
        startActivity(intent);

    }
    // To go to Battery Activity after clicking on the OnClickBattery button
    public void OnClickBattery(View V) {

        Intent intent = new Intent(this, Battery.class);
        startActivity(intent);

    }



// Camera code function following lines of codes:
    private Uri fileUri1;
    //define a Uri

    public static final int MEDIA_TYPE_IMAGE = 1;

    //define the state of image
    public void OnClickTakePhoto(View V) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //intent the existing camera application and return control to the calling application
        fileUri1 = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        //get the uri of a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri1);
        //specifying the path and file name for the recieved image
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        //start to intent the image capture activity

    }

    //Create the file uri
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
        //uri is defined by image file
    }

    //Create a File for saving an image or video
    @SuppressLint("SimpleDateFormat")
    private static File getOutputMediaFile(int type) {
        final File mediaStorageDir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM), "CompassApp");
        } else {
            mediaStorageDir = new File("/storage/sdcard0/CompassApp/");
        }
        //Creating the storage directory.If SD card exists, create a directory of standard,
        // shared and recommend location for saving pictures
        //If not , create directory in the device
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                //Create the directory name by this file , creating missing parents directories if necessary.
                Log.d("CompassApp", "failed to create directory");
                return null;
            }
        }


        //Create a media file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = null;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    //present the state of saved image

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image successfully saved", Toast.LENGTH_SHORT).show();
            } else if (requestCode == RESULT_CANCELED) {
                //User cancelled the image caputure
            } else {
                //Image caputre failed, advice user
                Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show();
            }
        }
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
