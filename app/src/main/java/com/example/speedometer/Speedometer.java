package com.example.speedometer;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Bundle;
import android.os.Looper;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class Speedometer extends AppCompatActivity {

    boolean flag10UP = true, flag30UP = true,  flag10Down = true, flag30Down = true  , checkIfStartBetwwn = true ;
    boolean checkIfDown = true , checkIfUp = true ;
    long firstTimeForUp = 0, firstTimeForDown = 0, lastTime, allTime;
    TextView txtVelocity, txtTimeUp, txtTimeDown;
    int PERMISSION_ID = 1;
    FusedLocationProviderClient mFusedLocationClient;
    float nCurrentSpeed = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtVelocity = findViewById(R.id.txtVelocity);
        txtTimeUp = findViewById(R.id.txtTimeUP);
        txtTimeDown = findViewById(R.id.txtTimeDown);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        //T Check User Agree Permission Or Not.
        if (checkPermissions()) {
            calculateVelocityTime();

        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void calculateVelocityTime() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, velocity,
                Looper.myLooper()
        );
    }


    private LocationCallback velocity = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();

                    // getSpeed and Print it in TextView
            txtVelocity.setText(String.format("%.2f", nCurrentSpeed) + " km/h");

                // Check If Start Between 10 and 30
            if(nCurrentSpeed < 10 && nCurrentSpeed != 0){

                checkIfStartBetwwn = false;
            }

            // Check if user get speed more than 10 and less 30 then get less than 10 again
            if(nCurrentSpeed <= 10 && firstTimeForUp != 0 && checkIfDown){
                flag10UP = true ;
            }


            // Check If user Stat with speed less than 10 and Calculate First Time User get 10Km/h
            if(nCurrentSpeed < 10 && nCurrentSpeed != 0){

                checkIfStartBetwwn = false;
            }
                    if (nCurrentSpeed >= 10 && flag10UP) {
                        firstTimeForUp = location.getTime() / 1000;
                        if(nCurrentSpeed > 10 && nCurrentSpeed < 30 && checkIfStartBetwwn){

                            firstTimeForUp = 0 ;
                        }else {
                            firstTimeForUp = location.getTime() / 1000;
                            flag10UP = false;
                        }
                }


                    // Calculate the time between (time of 10 and time of 30 )
                if (nCurrentSpeed >= 30 && flag30UP && firstTimeForUp != 0 && checkIfDown) {
                    lastTime = location.getTime() / 1000;
                    allTime = lastTime - firstTimeForUp;
                    txtTimeUp.setText(allTime + " S");
                    flag30UP = false;
                    checkIfDown = false;
                    checkIfUp = true;
                    flag10Down = flag30Down = true;

                }

            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            // Check if user get speed less than 30 and more 10 then get more than 30 again
            if(nCurrentSpeed >= 30 && firstTimeForDown !=0 && checkIfUp) {
                flag30Down = true ;
            }

            // Check If user Stat with speed  30 and Calculate First Time User get 30Km/h
            if ( nCurrentSpeed <= 30 && flag30Down ) {
                firstTimeForDown = location.getTime() / 1000;
                flag30Down = false;

            }

            // Calculate the time between (time of 30 and time of 10 )
                if (nCurrentSpeed <= 10 && flag10Down && firstTimeForDown != 0 && checkIfUp) {
                    lastTime = location.getTime() / 1000;
                    allTime = lastTime - firstTimeForDown;
                    txtTimeDown.setText(allTime + " S");
                    checkIfUp = false ;
                    checkIfDown = true ;
                    flag10Down = false;
                    flag10UP = flag30UP = true;

            }

            nCurrentSpeed = location.getSpeed() * 3.6f;


        }
    };



    //This method will tell us whether or not the user grant us to access ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION.
    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    //This method will request our necessary permissions to the user if they are not already granted.
    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    //This method is called when a user Allow or Deny our requested permissions. So it will help us to move forward if the permissions are granted.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {


            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
            }
        }
    }
}
