package hackattack.me.hackattack;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class MainGameActivity extends AppCompatActivity {

    public Context context;
    private Camera mCamera;
    private CameraPreview mPreview;
    public FrameLayout preview;
    double x1, x2, y1, y2 = 0;
    double distance = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);

        context = this;

        preview = (FrameLayout) findViewById(R.id.camera_view);

        boolean firstPlayer = getIntent().getBooleanExtra("firstPlayer", false);

        if(firstPlayer){
            ((TextView)findViewById(R.id.runText)).setVisibility(View.INVISIBLE);
        }else{
            ((TextView)findViewById(R.id.waitText)).setVisibility(View.INVISIBLE);
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCamera = getCameraInstance();
                mPreview = new CameraPreview(context, mCamera);
                preview.addView(mPreview);
            }
        }, 10000);

        final int gameCode = getIntent().getIntExtra("gameCode", -1);

        Log.i("MainGame", "Game code " + gameCode);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        DatabaseReference gameRef = database.child("games").child(Integer.toString(gameCode));
        gameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("MainGame", "Game data changed, recalculating distance");
                Log.i("MainGame", "Game data " + dataSnapshot);
                Map<String, Double> map = (Map<String, Double>) dataSnapshot.getValue();
                Log.i("MainGame", "" + map);
                Log.i("MainGame", "" + map.get("player2coordx"));

                try {
                    x1 = map.get("player1coordx");
                    y1 = map.get("player1coordy");
                    x2 = map.get("player2coordx");
                    y2 = map.get("player2coordy");
                    distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
                }catch(Exception e){
                    Log.i("ERROR", "asdf");
                }

                Log.i("MainGame", "values " + x1 + " " + y1 + " " + y2 + " " + y2);
                Log.i("MainGame", "distance " + distance);

                handleDistance();
            }

            @Override
            public void onCancelled(DatabaseError error){
                Log.i("MainGame", "Get rect");
            }
        });

    }

    public void handleDistance(){
        //distance < 1
        if(distance < 1){
            //1 dot
        }if(distance < .5){
            //2 dots
        }if(distance < .25){
            //3 dots
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
//
//    public void releaseCamera(){
//        mPreview.releaseCamera();
//        if(mCamera !=null){
//            mCamera.stopPreview();
//            mCamera.setPreviewCallback(null);
//
//            mCamera.release();
//            mCamera = null;
//        }
//    }
}
