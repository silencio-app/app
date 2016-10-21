package io.github.silencio_app.silencio;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private MediaRecorder mediaRecorder = null;
    private String MSG = "Main Thread Logging header";
    private TextView amplitude; // TextFiled for showing textual reading
    private boolean recording_flag = false; // Boolean to check if graph has to be plot or not
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;  // Pointer for plotting the amplitude
    private double amp_ref = 3.27;

    /**
     * Function called when start button is pressed
     * @param view
     */
    public void startMIC(View view){

        /**
         * start the MIC if mediaRecorder instance is created else Pops up a message
         */
        // previously invisible view
        View myView = findViewById(R.id.graph);

        // get the center for the clipping circle
        int cx = myView.getWidth() / 2;
        int cy = myView.getHeight() / 2;

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        myView.setVisibility(View.VISIBLE);
        anim.start();

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

            }
            catch (IOException e){
                Log.d(MSG, "================== EXCEPTION ================");
                e.printStackTrace();
            }

        }
    }

    public void stopMIC(View view) {

        // previously visible view
        final View myView = findViewById(R.id.graph);

        // get the center for the clipping circle
        int cx = myView.getWidth() / 2;
        int cy = myView.getHeight() / 2;

        // get the initial radius for the clipping circle
        float initialRadius = (float) Math.hypot(cx, cy);

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();

        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            recording_flag = false; //reset the flag
        }
        else{
            Log.d(MSG, "================== NO MIC LOCKED ================");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amplitude = (TextView)findViewById(R.id.amp);
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
        viewport.setMaxX(10);  // 10 units frame
        viewport.setScalable(true); // auto scroll to right
    }

    /**
     * private class for fetching amplitude and mapping graph
     */
    private class AudioListener implements Runnable{
        private String MSG = "AudioListener Thread Logging header : ";
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
                final int amp_val = getAmplitude();
                final String amp_val_string = amp_val + "";
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        amplitude.setText(amp_val_string);
                        series.appendData(new DataPoint(lastX++, amp_val), true, 100);
                    }
                });
                Log.d(MSG, " === AMPLITUDE === "+ amp_val_string);
                try {
                    // Sleep for 600 ms for next value
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
