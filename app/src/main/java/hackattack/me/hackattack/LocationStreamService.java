package hackattack.me.hackattack;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LocationStreamService extends Service {
    public LocationStreamService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
