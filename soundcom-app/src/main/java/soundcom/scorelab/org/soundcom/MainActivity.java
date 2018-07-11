/**
 * Created by user on 29-05-2018.
 */
package soundcom.scorelab.org.soundcom;
import android.content.ContextWrapper;
import android.content.Intent;
import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import soundcom.scorelab.org.soundcom_lib.Receiver;
import soundcom.scorelab.org.soundcom_lib.Transmitter;

import static android.R.id.message;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static Transmitter transmitter;
    private static Receiver receiver;
    private static String modulation;
    private static ImageView image;
    private static String src;
    private static double duration;
    private static double sample_rate;
    private static double symbol_size;
    private static double sample_period;
    private static int number_of_carriers;
    private static MediaPlayer mediaplayer;

    private static String recovered_string;
    private static String a;
    private static TextView name;
    private static TextView mail;
    private static EditText mEdit;
    private static Button gen;
    private static FloatingActionButton fab_trans;
    private static TextView recovered_textView;
    private static String user;
    private MessageAdapter messageAdapter;
    private ListView messagesView;
    private static String TAG = "PermissionDemo";           // Permissions to write to files
    private static final int REQUEST_WRITE_STORAGE = 112;
    private String filename = "storage.json";
    private String filepath = "MyFileStorage";
    private File myInternalFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        messageAdapter = new MessageAdapter(this);

        setContentView(R.layout.home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        View send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clicked(v);
            }

        });
        View receive = findViewById(R.id.receive);
        receive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clicked(v);
            }

        });

        image = new ImageView(this);
        image.setImageResource(R.drawable.transmit);


        modulation = "FSK";
        sample_rate = 44100.0;
        symbol_size = 0.25;
        sample_period = 1.0 / sample_rate;
        duration = 36;//duration = src.length * 16 * symbol_size /7
        number_of_carriers = 16;
        name = (TextView) findViewById(R.id.username);
        mail = (TextView) findViewById(R.id.textView);
        requestNamePermissions();
        requestWritePermissions();
        requestRecordPermissions();
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir(filepath, Context.MODE_PRIVATE);
        myInternalFile = new File(directory , filename);

        a = name.getText().toString();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        check();
    }

    public void clicked(View v) {
        if (v.getId() == R.id.send) {
            initTransmit();
        } else if (v.getId() == R.id.receive) {
            initReceive();
        }
    }
    public void check(){
        boolean isFilePresent = isFilePresent(this, "storage.json");
        if(!isFilePresent) {
            boolean isFileCreated = create(this, "storage.json", "{}");
            if(isFileCreated) {
                //proceed with storing the first todo  or show ui
                System.out.println("json file created");
            } else {
                //show error or try again.
                System.out.println("json file creation failed");
            }
        }
    }

    public String getRandomColor() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer("#");
        while (sb.length() < 7) {
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 7);
    }

    //public void test(){System.out.println("Working");}
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.generate) {

            final Context context = getApplicationContext();
            src = mEdit.getText().toString();
            while (src.length() != 30) {
                src += " ";
            }
            generate(context);
            fab_trans.show();

        }
    }

    public void requestRecordPermissions() {
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to Record Audio denied");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Permission to Record Audio")
                        .setTitle("Permission required");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG, "Clicked");
                        makeRecordRequest();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                makeRecordRequest();
            }
        }
    }

    public void requestNamePermissions() {
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.GET_ACCOUNTS);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to Username denied");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.GET_ACCOUNTS)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Permission to username")
                        .setTitle("Permission required");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG, "Clicked");
                        makeNameRequest();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                makeNameRequest();
            }
        }
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");
            mail.setText(email);
            if (parts.length > 1)
                name.setText(parts[0]);
        }
    }



    protected void makeRecordRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_WRITE_STORAGE);
    }
    protected void makeNameRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.GET_ACCOUNTS},
                REQUEST_WRITE_STORAGE);
    }

    public void requestWritePermissions() {
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Permission to access the SD-CARD is required for this app to Download PDF.")
                        .setTitle("Permission required");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG, "Clicked");
                        makeWriteRequest();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                makeWriteRequest();
            }
        }

    }

    protected void makeWriteRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_STORAGE);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user");

                } else {

                    Log.i(TAG, "Permission has been granted by user");

                }
                return;
            }
        }
    }

    private boolean create(Context context, String fileName, String jsonString){

        try {
            FileOutputStream fOut = openFileOutput(fileName,Context.MODE_PRIVATE);
            fOut.write(jsonString.getBytes());
            fOut.close();
            return true;
        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        }

    }

    public boolean isFilePresent(Context context, String fileName) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        System.out.println(path);
        return file.exists();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.transmitter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.receive) {
            initReceive();

        } else if (id == R.id.transmit) {

            initTransmit();

        } else if (id == R.id.history){
            initHistory();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initReceive() {

        final Context context = getApplicationContext();
        CharSequence text = "Receiver Mode Activated";
        int duration = Toast.LENGTH_SHORT;

        long free = Runtime.getRuntime().freeMemory();


        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        setContentView(R.layout.activity_receiver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        recovered_textView = (TextView) findViewById(R.id.recovered);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Receiving Modulated Waveform", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                try {

                    record(context);


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.receive);


    }
        public void initHistory() {

        final Context context = getApplicationContext();
        CharSequence text = "Your Recent Chat";
        int duration = Toast.LENGTH_SHORT;

        long free = Runtime.getRuntime().freeMemory();


        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        setContentView(R.layout.history_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.history);
        System.out.println("History inited");
        //check();
            messageAdapter = new MessageAdapter(this);

            try {
            System.out.println(loadJSONFromAsset());
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray messages = obj.getJSONArray("messages");
            //JSONArray messages = new JSONArray(loadJSONFromAsset());
            for (int i = 0; i < messages.length(); i++) {
                JSONObject jo_inside = messages.getJSONObject(i);
                String textvalue = jo_inside.getString("text");
                JSONObject data = jo_inside.getJSONObject("data");
                MemberData datavalue = new MemberData(data.getString("name"),data.getString("color"));
                boolean iscurrentuser = Boolean.valueOf(jo_inside.getString("user"));
                //Add your values in your `ArrayList` as below:
                final Message message = new Message(textvalue,datavalue,iscurrentuser);
                messagesView = (ListView) findViewById(R.id.messages_view);
                messagesView.setAdapter(messageAdapter);
                //messages.add(message);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageAdapter.add(message);
                        messagesView.setSelection(messagesView.getCount() - 1);
                        System.out.println("message added");
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void initTransmit() {
        final Context context = getApplicationContext();

        CharSequence text = "Transmitter Mode Activated";
        int duration = Toast.LENGTH_SHORT;

        long free = Runtime.getRuntime().freeMemory();
//        generate(context);

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        setContentView(R.layout.activity_transmitter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.transmit);


        gen = (Button) findViewById(R.id.generate);
        gen.setOnClickListener(this);
        mEdit = (EditText) findViewById(R.id.transmitString);


        fab_trans = (FloatingActionButton) findViewById(R.id.fab);
        fab_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Transmitting Modulated Waveform", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                mediaplayer = new MediaPlayer();


                String root = Environment.getExternalStorageDirectory().toString();
                File dir = new File(root, "Soundcom");
                if (!dir.exists()) {
                    dir.mkdir();
                }

                try {
                    mediaplayer.setDataSource(dir + File.separator + "FSK.wav");
                    mediaplayer.prepare();
                    mediaplayer.start();
                    name = (TextView) findViewById(R.id.name);
                    a=name.getText().toString();
                    MemberData data = new MemberData(a, getRandomColor());
                    boolean belongsToCurrentUser=true;
                    final Message message = new Message(src, data, belongsToCurrentUser);
                    printmessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        fab_trans.hide();


    }

    public void generate(final Context context) {
        final ProgressDialog mProgressDialog = ProgressDialog.show(this, "Hold Tight", "Generating Modulation Data", true);


        new Thread() {
            @Override
            public void run() {

                transmitter = new Transmitter(modulation, src, sample_rate, symbol_size, sample_period, number_of_carriers, context);
                System.out.println("Writing WavFile");
                transmitter.writeAudio();
                System.out.println("WaveFile Written. Thread waiting");


                try {

                    // code runs in a thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.dismiss();
                        }
                    });
                } catch (final Exception ex) {

                }
            }
        }.start();
    }


    public void record(final Context context) {
        final ProgressDialog mProgressDialog = ProgressDialog.show(this, "Hold Tight", "Recovering Signal", true);
        new Thread() {
            @Override
            public void run() {
                try {
                    long startTime = System.nanoTime();

                    receiver = new Receiver("recorded.wav", sample_rate, symbol_size, duration, number_of_carriers, context);
                    receiver.record();
                    receiver.demodulate();
                    recovered_string = "aaaa";
                    recovered_string = receiver.getRecoverd_string();


                    long endTime = System.nanoTime();
                    long duration = (endTime - startTime) / 1000000;
                    System.out.println("Time taken for Reception: " + duration + "ms");

//                    final TextView textViewToChange = (TextView) findViewById(R.id.recovered);
//                    textViewToChange.setText(recovered);


                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {

                    // code runs in a thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.dismiss();
                            if(recovered_string.equals("-1")){
                                recovered_string = " ";
                                CharSequence text = "Never caught that \uD83D\uDE22 \nPlease Could You Retransmit";
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                            else{
                                MemberData data = new MemberData(recovered_string.substring(0,9), getRandomColor());
                                boolean belongsToCurrentUser=false;
                                final Message message = new Message(recovered_string, data, belongsToCurrentUser);
                                printmessage(message);
                            }
                            recovered_textView.setText(" " + recovered_string + " ");


                        }
                    });
                } catch (final Exception ex) {

                }
            }
        }.start();

    }

public void printmessage(final Message message){
    initHistory();

    messagesView = (ListView) findViewById(R.id.messages_view);
    messagesView.setAdapter(messageAdapter);
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            messageAdapter.add(message);
            messagesView.setSelection(messagesView.getCount() - 1);
            System.out.println("message added");
        }
    });
    int x=messageAdapter.getCount();
    List<Message> messages= messageAdapter.getmessages();
    JsonUtil jsonstring= new JsonUtil();
    String JSONcontent = jsonstring.toJSon(messages,x);
    if(isFilePresent(this, "storage.json")) {
        try {
            //FileOutputStream fOut = openFileOutput("storage.json",Context.MODE_PRIVATE);
            FileOutputStream fOut = new FileOutputStream(myInternalFile);
            fOut.write(JSONcontent.getBytes());
            fOut.close();
            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        }
        //proceed with storing the first todo  or show ui
    } else {
        create(this, "storage.json", "{}");
        printmessage(message);
        //show error or try again.
    }

}
    public String loadJSONFromAsset() {
        String json = "";
       /* try {
            FileInputStream fin = openFileInput("storage.json");
            int c;
            while( (c = fin.read()) != -1){
                json = json + Character.toString((char)c);
            }

            fin.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        */
        try {
            FileInputStream fis = new FileInputStream(myInternalFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                json = json + strLine;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
}
class JsonUtil {

    public static String toJSon(List<Message> messages , int n) {
        try {
            // Here we convert Java Object to JSON
            JSONObject messageslist= new JSONObject();
            JSONArray Jarray= new JSONArray();
            for(int i=0;i<n;i++) {
                JSONObject jsonTxt = new JSONObject();
                jsonTxt.put("text", messages.get(i).getText()); // Set the first name/pair

                JSONObject jsondata = new JSONObject(); // we need another object to store the address
                jsondata.put("name", messages.get(i).getData().getName());
                jsondata.put("color", messages.get(i).getData().getColor());

                // We add the object to the main object
                jsonTxt.put("data", jsondata);

                // and finally we add the phone number
                // In this case we need a json array to hold the java lis

                jsonTxt.put("user", String.valueOf(messages.get(i).isBelongsToCurrentUser()));
                Jarray.put(jsonTxt);
            }
            messageslist.put("messages",Jarray);

            System.out.println(messageslist.toString());
            return messageslist.toString();

        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return null;

    }
}
class MemberData {
    private String name;
    private String color;

    public MemberData(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public MemberData() {
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "MemberData{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
