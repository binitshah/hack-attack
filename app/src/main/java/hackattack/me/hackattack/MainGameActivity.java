package hackattack.me.hackattack;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class MainGameActivity extends AppCompatActivity {

    public Context context;
    private Camera mCamera;
    private CameraPreview mPreview;
    public FrameLayout preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);

        context = this;

        preview = (FrameLayout) findViewById(R.id.camera_view);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCamera = getCameraInstance();
                mPreview = new CameraPreview(context, mCamera);
                preview.addView(mPreview);
            }
        }, 1000);

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
