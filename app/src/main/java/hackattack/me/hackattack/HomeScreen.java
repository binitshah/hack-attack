package hackattack.me.hackattack;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ObjectStreamField;
import java.util.Map;
import java.util.Random;

public class HomeScreen extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    public Context context;
    public Activity activity;
    public FrameLayout preview;
    Intent openGame;
    Intent hostService;
    int randomCode = -1;
    View joinDialogView;
//    public Button joinbutton;
//    public EditText serverCode;
    DatabaseReference database;
    DataSnapshot snap;
    Map<String, Object> gamesMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

       // gamesMap = Map<String, Obj>

        database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userList = database.child("games");
        //TRY ADD CHILD EVENT LISTENER
        userList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gamesMap = (Map<String, Object>) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Random jimmy = new Random();
        hostService = new Intent(this, LocationStreamService.class);
        randomCode = jimmy.nextInt(899)+100;
        hostService.putExtra("gameCode", randomCode);

        openGame = new Intent(this, MainGameActivity.class);
        context = this;
        activity = this;

        preview = (FrameLayout) findViewById(R.id.camera_preview);

        // Create an instance of Camera
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(this, mCamera);
        preview.addView(mPreview);

        Button hostbutton = (Button) findViewById(R.id.hostbutton);
        Button joinbutton = (Button) findViewById(R.id.joinbutton);
        //serverCode = (EditText) findViewById(R.id.serverCode);
        //serverCode.setVisibility(View.INVISIBLE);

        hostbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(hostService);
                new AlertDialog.Builder(context)
                    .setTitle("Host")
                    .setMessage("Server code: " + Integer.toString(randomCode))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
//                            releaseCamera();
                            startActivity(openGame);
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            }
        });

        joinbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                serverCode.setVisibility(View.VISIBLE);
//                joinbutton.setVisibility(View.INVISIBLE);


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = activity.getLayoutInflater();
                joinDialogView = inflater.inflate(R.layout.dialog_join, null);
                builder.setView(joinDialogView)
                        // Add action buttons
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // sign in the user ...
//                                releaseCamera();
                                EditText gameCodeInput = (EditText) joinDialogView.findViewById(R.id.gameCode);
                                String gameCode = gameCodeInput.getText().toString();

                                Object gcResult = (Map<String, Object>) gamesMap.get(gameCode);

                                if(gcResult == null){
                                    Toast.makeText(activity, "Game not found", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(activity, "Found game...", Toast.LENGTH_SHORT).show();
                                    startActivity(openGame);
                                    finish();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
            }
        });
    }
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.e("Camera error", "Could not open camera");
        }
        return c; // returns null if camera is unavailable
    }
    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        releaseCamera();
    }
    public void releaseCamera(){
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);

            mCamera.release();
            mCamera = null;
        }
    }
}
