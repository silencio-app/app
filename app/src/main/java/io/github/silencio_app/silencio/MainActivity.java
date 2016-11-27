package io.github.silencio_app.silencio;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.media.MediaRecorder;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Formatter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private MediaRecorder mediaRecorder = null;
    private final String MSG = "Main Thread Logging";
    private TextView amplitude; // TextFiled for showing textual reading
    private boolean recording_flag = false; // Boolean to check if graph has to be plot or not
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;  // Pointer for plotting the amplitude
    private static final double amp_ref = 3.27;
    private static final String PREVIOUS_X = "Previous x axis point";
    private static final String PREVIOUS_dB = "Previous noted decibals ";
    private int db_level; // decibel levels
    private boolean PLAY_PAUSE_STATUS = false;
    private Button play_pause_button;
    private ImageView loud_image;
    private WifiManager mWifiManager;
    private DhcpInfo dhcpInfo;
    private TextView current_location;

    private String current_ip;
    private final String FILENAME = "myFingerprinting";
    private EditText location_name;
    private boolean canR, canW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        amplitude = (TextView)findViewById(R.id.amp);
//        db_meter = (ProgressBar)findViewById(R.id.db_meter);
        play_pause_button = (Button)findViewById(R.id.play_pause_button);
        loud_image = (ImageView)findViewById(R.id.loud_image);
        current_location = (TextView)findViewById(R.id.current_location);
        location_name = (EditText)findViewById(R.id.location_name);

        /**
         *  Initialising the empty graph
         */
        GraphView graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<>();
        graph.addSeries(series);
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);  // min value is 0
        viewport.setMaxY(100);  // max value is 32768
        viewport.setMaxX(100);  // 10 units frame
        viewport.setScalable(true); // auto scroll to right

        Thread newT2 = new Thread(new IPMapper());  // New Thread is created to handle the amplitude fetching and plotting graph
        newT2.start();


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
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_task_home) {
//            Intent intent = new Intent(this, TaskHomeActivity.class);
//            startActivity(intent);
        } else if (id == R.id.nav_server) {
            Intent intent = new Intent(this, ServerListnerActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_account_setting) {
//            Intent intent = new Intent(this, SettingActivity.class);
//            startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Function called when start button is pressed
     * @param view
     */
    public void play_pause_handler(View view){
        if (!PLAY_PAUSE_STATUS){
            // If Mic is not running
            startMIC(view);
            PLAY_PAUSE_STATUS = true;
            play_pause_button.setBackground(getResources().getDrawable(R.drawable.ic_menu_slideshow));
        }
        else{
            // If Mic is running
            stopMIC(view);
            PLAY_PAUSE_STATUS = false;
            play_pause_button.setBackground(getResources().getDrawable(R.drawable.ic_menu_send));
        }

    }
    public void showGraph(View view){
        View myView = findViewById(R.id.graph);

        // get the center for the clipping circle
        int cx = myView.getWidth() / 2;
        int cy = myView.getHeight() / 2;

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

        // make the graph and meter visible and start the animation
        myView.setVisibility(View.VISIBLE);
//        db_meter.setVisibility(View.VISIBLE);
        anim.start();
    }
    public void hideGraph(View view){
        TextView tview = (TextView) findViewById(R.id.amp);
        tview.setText(getString(R.string.press_start));

        // previously visible view
        final View myView = findViewById(R.id.graph);

        // get the center for the clipping circle
        int cx = myView.getWidth() / 2;
        int cy = myView.getHeight() / 2;

        // get the initial radius for the clipping circle
        float initialRadius = (float) Math.hypot(cx, cy);

        // create the animation (the final radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // make graph and meter invisible
                myView.setVisibility(View.INVISIBLE);
//                db_meter.setVisibility(View.GONE);
            }
        });

        // start the animation
        anim.start();
    }

    public void startMIC(View view){

        /**
         * start the MIC if mediaRecorder instance is created else Pops up a message
         */
        if(mediaRecorder == null){
            mediaRecorder = new MediaRecorder();
            mediaRecorder.reset();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile("/dev/null"); // Not saving the audio

            try{
                mediaRecorder.prepare();
                mediaRecorder.start();
                recording_flag = true;

                Thread newT = new Thread(new AudioListener());  // New Thread is created to handle the amplitude fetching and plotting graph
                newT.start();
                showGraph(view);

            }
            catch (IOException e){
                Log.d(MSG, "================== EXCEPTION ================");
                e.printStackTrace();
            }

        }
    }

    public void stopMIC(View view) {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            recording_flag = false; //reset the flag
            hideGraph(view);
        }
        else{
            Log.d(MSG, "================== NO MIC LOCKED ================");
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putInt(PREVIOUS_X, lastX);
        savedInstanceState.putInt(PREVIOUS_dB, db_level);
        super.onSaveInstanceState(savedInstanceState);
    }

    public boolean isExternalWritable(){
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
    public boolean isExternalReadable(){
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
    public void externalState(){
        if (isExternalReadable() && isExternalWritable()){
            canR = canW = true;
        }
        else if (isExternalReadable() && !isExternalWritable()){
            canR = true;
            canW = false;
        }
        else{
            canR = canW = false;
        }
    }
    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), albumName);
        if (!file.mkdirs()) {
            file.mkdirs();
        }
        return file;
    }
    public void bind_ip_value_to_location(View view){
        Log.d(MSG, "======================== YES I HAVE BEEEN  CALLED ===============================");
        FileOutputStream fos;
        String name = location_name.getText().toString();

        File file;
        externalState();
        if(canW && canR){
            FileOutputStream outputStream;
            try {
                file = new File(getAlbumStorageDir("Files Generated"), FILENAME+".txt");

                outputStream = new FileOutputStream(file, true);
                outputStream.write(name.getBytes());
                outputStream.write(" == ".getBytes());
                outputStream.write(current_ip.getBytes());
                outputStream.write("\n".getBytes());
                outputStream.close();
                Toast.makeText(getApplicationContext(), "SAVED", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Cannot Write to External Now", Toast.LENGTH_SHORT).show();
        }

    }

    private class IPMapper implements Runnable{

        public void get_gateway_ip(){

            mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if(mWifiManager.isWifiEnabled()){
                WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                if (wifiInfo.getNetworkId() == -1){
                    // Not connected to an access point
                    Toast.makeText(getApplicationContext(), " You are not connected to any access point", Toast.LENGTH_LONG).show();
                }
                else{
                    // Connected to access point
                    dhcpInfo = mWifiManager.getDhcpInfo();
                    int gateway = dhcpInfo.gateway;
                    String binary_string = Integer.toBinaryString(gateway);
                    int len = binary_string.length();
                    String oct1, oct2, oct3, oct4;
                    oct1 = binary_string.substring(len - 8, len);
                    oct2 = binary_string.substring(len - 16, len - 8);
                    oct3 = binary_string.substring(len - 24, len - 16);
                    oct4 = binary_string.substring(0, len - 24);
                    Log.d(" MSG ", " =========== Connected to "+ Integer.parseInt(oct1, 2) +"."+ Integer.parseInt(oct2, 2) + "."+Integer.parseInt(oct3, 2)+"."+Integer.parseInt(oct4, 2));
                    current_ip = Integer.parseInt(oct1, 2) +"."+ Integer.parseInt(oct2, 2) + "."+Integer.parseInt(oct3, 2)+"."+Integer.parseInt(oct4, 2);
                }
            }
            else{

                Toast.makeText(getApplicationContext(), " Wifi Not Enabled", Toast.LENGTH_LONG).show();
            }
        }
        @Override
        public void run() {
            while(true){
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        get_gateway_ip();
                        current_location.setText(current_ip);
                    }
                });
                try {
                    // Sleep for 600 ms for next value
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * private class for fetching amplitude and mapping graph
     */
    private class AudioListener implements Runnable{
        private final String MSG = "AudioListener Logging: ";
        /**
         * @return current amplitude if instance of MIC exist
         */
        public int getAmplitude() {
            if (mediaRecorder != null) {
                double y =  20*Math.log10(mediaRecorder.getMaxAmplitude()/amp_ref);
                return (int)y;
            }
            else
            {
                return -1;
            }
        }
        @Override
        public void run(){

            // if recording flag is true then keep mapping graph
            while(recording_flag){
                int raw_amp_val = getAmplitude();
                final int amp_val;
                if (raw_amp_val < 0){
                    amp_val = 0;
                }
                else{
                    amp_val = raw_amp_val;
                }
                db_level = amp_val;
                final String amp_val_string = amp_val + "";
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        amplitude.setText(amp_val_string);
//                        db_meter.setProgress(amp_val);

                        /*
                        Provide Style to meter according to decibel values
                         */
                        if (amp_val <= 50){
                            loud_image.setBackground(getResources().getDrawable(R.drawable.sound_level_1));
//                            db_meter.setProgressDrawable(getDrawable(R.drawable.greenprogress));
                        }
                        if (amp_val > 50 && amp_val <= 70){
                            loud_image.setBackground(getResources().getDrawable(R.drawable.sound_level_2));
//                            db_meter.setProgressDrawable(getDrawable(R.drawable.orangeprogress));
                        }
                        if (amp_val > 70){
                            loud_image.setBackground(getResources().getDrawable(R.drawable.sound_level_3));
//                            db_meter.setProgressDrawable(getDrawable(R.drawable.redprogress));
                        }
                        series.appendData(new DataPoint(lastX++, amp_val), true, 100);
                    }
                });
                Log.d(MSG, " === AMPLITUDE === "+ amp_val_string);
//                long startTime = System.nanoTime();
//                long endTime = System.nanoTime();
//                long duration = (endTime - startTime)/1000000;
//                Log.d("MSG", " Time took is ============== "+duration);


                try {
                    // Sleep for 600 ms for next value
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d(MSG, "======== Thread Destroyed =========");
        }
    }
}
