package soundcom.scorelab.org.soundcom;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.dagger.Provides;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.view.View;


import static org.junit.Assert.*;
import static soundcom.scorelab.org.soundcom.R.styleable.View;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest{
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("soundcom.scorelab.org.soundcom", appContext.getPackageName());
    }
    private String src;
    private static EditText mEdit;

   @Test
   public void generateTest(){  //ensure string length is 30, generate button test
       final Activity activity = activityTestRule.getActivity();
       activity.runOnUiThread(new Runnable() {

           @Override
           public void run() {

               MainActivity obj= new MainActivity();
               LayoutInflater inflater = activity.getLayoutInflater();
               View myView = inflater.inflate(R.layout.activity_transmitter, null);
               mEdit = (EditText) myView.findViewById(R.id.transmitString);
               Log.d("edit text at ", mEdit.getText().toString());
               mEdit.setText("Hello");
               Log.d("after change edit text at ", mEdit.getText().toString());

               Context context=activity.getBaseContext();
               View test= myView.findViewById(R.id.generate);
               obj.clickHelper(context,mEdit, test);
               Log.d("value after test ", mEdit.getText().toString());
               src=mEdit.getText().toString();
               assertEquals(src.length(),30);
           }
       });

   }

}
