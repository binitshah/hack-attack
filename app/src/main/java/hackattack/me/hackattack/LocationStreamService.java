package hackattack.me.hackattack;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class LocationStreamService extends Service {
    public LocationStreamService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        String gameCode = Integer.toString(intent.getIntExtra("gameCode", -1));
        if(game == null) {
            Log.i("Service game code: ", gameCode);
            Log.i("Service", "Setting ref value");
            game = new GameObj(Integer.parseInt(gameCode));
            database.child("games").child(gameCode).setValue(game);
        }
        return Service.START_STICKY;
    }
    GameObj game;
    DatabaseReference database;
    @Override
    public void onCreate() {
        Log.i("Started service", "Connecting to database");
        database = FirebaseDatabase.getInstance().getReference();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int y;
                while (true) {
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