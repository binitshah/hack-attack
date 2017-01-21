package hackattack.me.hackattack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        Button hostbutton = (Button) findViewById(R.id.hostbutton);

        hostbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //where the server code goes
            }
        });
    }
}
