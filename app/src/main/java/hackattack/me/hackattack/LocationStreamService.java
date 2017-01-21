package hackattack.me.hackattack;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationStreamService extends Service {
    public LocationStreamService() {
    }
    DatabaseReference myRef;
    @Override
    public void onCreate() {

        Log.i("Started service", "Connecting to database");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    Log.i("Service", "Setting ref value");
                    myRef.setValue("Hello, World!");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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