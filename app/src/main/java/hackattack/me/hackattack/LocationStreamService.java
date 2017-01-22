package hackattack.me.hackattack;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LocationStreamService extends Service implements LocationListener {

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    Location location; // location

    LocationManager locationManager;

    String gameCode = "-1";
    boolean firstPlayer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        gameCode = Integer.toString(intent.getIntExtra("gameCode", -1));
        firstPlayer = intent.getBooleanExtra("firstPlayer", false);
        if (firstPlayer) {
            Log.i("Service game code: ", gameCode);
            Log.i("Service", "Setting ref value");
            GameObj game = new GameObj(Integer.parseInt(gameCode));
            database.child("games").child(gameCode).setValue(game);
        }
        getLocation();
        return Service.START_STICKY;
    }

    DatabaseReference database;

    @Override
    public void onCreate() {
        Log.i("Started service", "Connecting to database");
        database = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Location getLocation() {
        Log.i("Service", "Getting location");
        try {
            locationManager = (LocationManager) getApplicationContext()
                    .getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if ((!isGPSEnabled && !isNetworkEnabled) || (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
                Toast.makeText(getApplicationContext()  .getApplicationContext(), "Location not enabled", Toast.LENGTH_SHORT).show();
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            0,
                            0, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                0,
                                0, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("Service", "Got location");
        updateLocationFb();
        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        updateLocationFb();
    }

    public void updateLocationFb(){
        Log.i("Service", "Update location " + gameCode);


        DatabaseReference gameRef = database.child("games").child(gameCode);
        Map<String,Object> updateMap = new HashMap<String,Object>();

        if(firstPlayer){
            Log.i("Service", "First player");
            updateMap.put("player1coordx", location.getLatitude());
            updateMap.put("player1coordy", location.getLongitude());
        }else{
            Log.i("Service", "Second player");
            updateMap.put("player2coordx", location.getLatitude());
            updateMap.put("player2coordy", location.getLongitude());
        }

        gameRef.updateChildren(updateMap);

    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

/*

myRef.addValueEventListener(new ValueEventListener() {
        @Override
public void onDataChange(DataSnapshot dataSnapshot) {

        String value = dataSnapshot.getValue(String.class);
        Log.d(TAG, "Value is: " + value);
        }

        @Override
public void onCancelled(DatabaseError error){

        Log.w(TAG, "Failed to read value.", error.toExeption());
        }


        });

*/