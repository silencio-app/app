package io.github.silencio_app.silencio;

import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private MediaRecorder mediaRecorder = null;
    private String MSG = "SILENCIO LOG: ";
    private TextView amplitude;
    private boolean recordFlag = false;

    public void startMic(View view){
        /**
         *  Function called when Start Button Pressed
         *  purpose: create an instance of MIC if not created yet
         */

        if(mediaRecorder == null){
            mediaRecorder = new MediaRecorder();
            mediaRecorder.reset();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile("/dev/null");

            try{
                mediaRecorder.prepare();
                mediaRecorder.start();
                recordFlag = true;
            }
            catch (IOException e){
                Log.d(MSG, "================== EXCEPTION ================");
                e.printStackTrace();
            }

        }
    }

    public void stopMic(View view) {
        /**
         *  Function called when Stop Button Pressed
         *  purpose: delete the instance of MIC if created
         */
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            recordFlag = false;
        }
        else{
            Log.d(MSG, "================== NO MIC LOCKED ================");
        }
    }
    public void showData(View view) throws InterruptedException {
        while(recordFlag){
            double value = getAmplitude();
            if(value == -1){
                Toast.makeText(getApplicationContext(), "Please Start MIC first", Toast.LENGTH_SHORT).show();
                recordFlag = false;
                Log.d(MSG, "================== MIC not started yet ================");
                break;
            }
            else{
                String val = value + "";
                amplitude.setText(val);
                Log.d(MSG, "================== "+ val +" ================");
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }
    public double getAmplitude(){
        /**
         *  Function called every 1 sec to fetch current Amplitude
         *  purpose: return current amplitude
         */

        if (mediaRecorder != null) {
            double y = mediaRecorder.getMaxAmplitude();
            /*double x = 20*Math.log10(mediaRecorder.getMaxAmplitude()/600);
            String x =  + "";*/
            return y;

        }
        else
        {
            return -1;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amplitude = (TextView)findViewById(R.id.amp);
    }
}
