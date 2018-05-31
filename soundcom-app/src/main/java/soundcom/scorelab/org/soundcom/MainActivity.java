package soundcom.scorelab.org.soundcom;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import soundcom.scorelab.org.soundcom_lib.AudioHandler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    // Example of a call to a native method
        //onClick();

        View myButton = findViewById(R.id.generate);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clicked(v);
            }

        });
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public void clicked(View v) {
        if(v.getId()== R.id.generate) {
                setContentView(R.layout.activity_main);
                TextView tv = (TextView) findViewById(R.id.sample_text);
                tv.setText(stringFromJNI());
                //break

        }
    }
    public String stringFromJNI(){return "Tested";}

    // Used to load the 'native-lib' library on application startup.

}
