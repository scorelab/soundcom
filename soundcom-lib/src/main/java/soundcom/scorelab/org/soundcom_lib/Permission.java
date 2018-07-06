/**
 * Created by user on 29-05-2018.
 */
package soundcom.scorelab.org.soundcom_lib;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;

public class Permission extends AppCompatActivity
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
    private static EditText mEdit;
    private static Button gen;
    private static FloatingActionButton fab_trans;
    private static TextView recovered_textView;


    private static String TAG = "PermissionDemo";           // Permissions to write to files
    private static final int REQUEST_WRITE_STORAGE = 112;


   @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        image = new ImageView(this);
        image.setImageResource(R.drawable.transmit);


        modulation = "FSK";
        sample_rate = 44100.0;
        symbol_size = 0.25;
        sample_period = 1.0 / sample_rate;
        duration = 36;//duration = src.length * 16 * symbol_size /7
        number_of_carriers = 16;


        requestWritePermissions();
        requestRecordPermissions();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


    }
        public void test(){
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        image = new ImageView(this);
        image.setImageResource(R.drawable.transmit);


        modulation = "FSK";
        sample_rate = 44100.0;
        symbol_size = 0.25;
        sample_period = 1.0 / sample_rate;
        duration = 36;//duration = src.length * 16 * symbol_size /7
        number_of_carriers = 16;


        requestWritePermissions();
        requestRecordPermissions();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

    }

    //public void test(){System.out.println("Working");}
    public void onClick(View v) {
        if(v.getId()==R.id.generate) {

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

    protected void makeRecordRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
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
                File dir = new File(root, "RedTooth");
                if (!dir.exists()) {
                    dir.mkdir();
                }

                try {
                    mediaplayer.setDataSource(dir + File.separator + "FSK.wav");
                    mediaplayer.prepare();
                    mediaplayer.start();
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
                            recovered_textView.setText(" " + recovered_string + " ");

                        }
                    });
                } catch (final Exception ex) {

                }
            }
        }.start();

    }


}
