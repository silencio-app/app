package io.github.silencio_app.silencio;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.media.MediaRecorder;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import static io.github.silencio_app.silencio.LoginActivity.PREFS_CURRENT_USER;
import static io.github.silencio_app.silencio.LoginActivity.PREFS_NAME;

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
    private TextView current_location;

    private String current_ip;
    private final String FILENAME = "myFingerprinting";
    private EditText location_name;
    private boolean canR, canW;
    private static final String DATETIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

    public Queue<NoiseRecord> recordQueue;
    private static final String POST_URL = "http://35.163.237.103/silencio/post/";
    private DateFormat dateFormat;
    private String CURRENT_LOGGED_USER;
    private static String CURRENT_LOCATION = "Hostel";

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
        play_pause_button = (Button)findViewById(R.id.play_pause_button);
        loud_image = (ImageView)findViewById(R.id.loud_image);
        current_location = (TextView)findViewById(R.id.current_location);
        location_name = (EditText)findViewById(R.id.location_name);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Somatic-Rounded.otf");
        amplitude.setTypeface(custom_font);

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

        recordQueue = new LinkedList<NoiseRecord>();
        dateFormat = new SimpleDateFormat(DATETIME_FORMAT);
        Thread newT2 = new Thread(new IPMapper());  // New Thread is created to handle the amplitude fetching and plotting graph
        newT2.start();

        CURRENT_LOGGED_USER = getIntent().getExtras().getString("LOGGING USER");
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREFS_CURRENT_USER, CURRENT_LOGGED_USER);
        editor.commit();
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
        if (id == R.id.action_exit) {
            finish();
            System.exit(0);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {}
        else if (id == R.id.nav_map) {
            Intent intent = new Intent(this, ServerListnerActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_share) {}
        else if (id == R.id.nav_logout) {
            CURRENT_LOGGED_USER = null;
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove(PREFS_CURRENT_USER);
            editor.commit();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
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
            play_pause_button.setBackground(getResources().getDrawable(R.drawable.ic_pause_circle_filled_white_24dp));
        }
        else{
            // If Mic is running
            stopMIC(view);
            PLAY_PAUSE_STATUS = false;
            play_pause_button.setBackground(getResources().getDrawable(R.drawable.ic_play_circle_filled_white_24dp));
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
                makeSnackbar("Saved");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            makeSnackbar("Cannot write to external storage now");
        }

    }

    public void makeSnackbar(String snackbarText) {
        Snackbar.make(getWindow().getDecorView().getRootView(), snackbarText, Snackbar.LENGTH_LONG)
                .setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {}
                })
                .show();
    }

    private class IPMapper implements Runnable{

        public void get_gateway_ip(){

            mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if(mWifiManager.isWifiEnabled()){
                WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                if (wifiInfo.getNetworkId() == -1){
                    // Not connected to an access point
                    makeSnackbar("You are not connected to any access point");
                }
                else{
                    current_ip = LocationMapper.get_location(wifiInfo.getBSSID());
                }
            }
            else{
                makeSnackbar("Wifi not enabled");
            }
        }
        @Override
        public void run() {
            while(true){
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        float av_db = 0;

                        if(recordQueue.size() > 100){
                            NoiseRecord record = recordQueue.remove();
                            av_db += record.getDb_level();
                            String start_date = record.getDate();
                            for(int i=0;i<98;i++){
                                av_db += recordQueue.remove().getDb_level();
                            }
                            record = recordQueue.remove();
                            av_db += record.getDb_level();
                            String end_date = record.getDate();
                            av_db /= 100;
                            NoiseRecordBundle noiseRecordBundle = new NoiseRecordBundle(record.getPlace(), av_db, start_date, end_date);
                            try {
                                String encodedUrl = "&username=" + URLEncoder.encode(CURRENT_LOGGED_USER, "UTF-8") +
                                        "&location=" + URLEncoder.encode(noiseRecordBundle.getPlace(), "UTF-8") +
                                        "&db_level=" + URLEncoder.encode(String.valueOf(noiseRecordBundle.getAvg_db()), "UTF-8") +
                                        "&start_time=" + URLEncoder.encode(String.valueOf(noiseRecordBundle.getStart()), "UTF-8") +
                                        "&end_time=" + URLEncoder.encode(String.valueOf(noiseRecordBundle.getEnd()), "UTF-8");
                                mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                                if(mWifiManager.isWifiEnabled()){
                                    WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                                    if (wifiInfo.getNetworkId() == -1){
                                        makeSnackbar("You are not connected to any access point");
                                    }
                                    else{
                                        new PostTask().execute(encodedUrl);
                                    }
                                }
                                else{
                                    makeSnackbar("WiFi not enabled");
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        get_gateway_ip();
                        current_location.setText(current_ip);
                    }
                });
                try {
                    // Sleep for 600 ms for next value
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class PostTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings)
        {
            String parameter = strings[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {

                URL url = new URL(POST_URL);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Accept", "application/x-www-form-urlencoded");

                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(parameter);
                writer.close();
                int response_code = urlConnection.getResponseCode();
                Log.d("DEBUGGER", "****************** RESPONSE CODE = "+response_code);

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine);
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }
                String return_value = buffer.toString();
                Log.d("RETURN", return_value);
                return return_value;
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("TAG RESPONSE", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*mDialog = new ProgressDialog(LoginActivity.this);
            mDialog.setTitle("Signing You In");
            mDialog.show();*/
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            /*handle_login(s);
            mDialog.dismiss();*/
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

                        NoiseRecord noiseRecord = new NoiseRecord("Hostel", (float)amp_val, dateFormat.format(new Date()));
                        recordQueue.add(noiseRecord);
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
