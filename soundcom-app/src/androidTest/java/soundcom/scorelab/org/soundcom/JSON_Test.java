package soundcom.scorelab.org.soundcom;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;


/**
 * Created by user on 06-08-2018.
 */
@RunWith(AndroidJUnit4.class)
public class JSON_Test {
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);
    private static String a;
    private static String s;
    @Test
    public void toJSONTest() {  //message conversion to JSON to store in file
        final Activity activity = activityTestRule.getActivity();
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String text = "HelloWorld";
                MemberData data = new MemberData("Piyush", "#123456");
                boolean user = true;
                Message message = new Message(text, data, user);
                List<Message> messages = new ArrayList<Message>();
                messages.add(message);
                JsonUtil json = new JsonUtil();
                s = json.toJSON(messages, 1);
                a = "{\"messages\":[{\"text\":\"HelloWorld\",\"data\":{\"name\":\"Piyush\",\"color\":\"#123456\"},\"user\":\"true\"}]}";
                assertEquals(s, a);
            }
        });
    }
}


