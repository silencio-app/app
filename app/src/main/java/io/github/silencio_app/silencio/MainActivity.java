package io.github.silencio_app.silencio;

import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private MediaRecorder mediaRecorder = null;
    private String MSG = "MIC";
    private TextView amplitude;

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
        }
        else{
            Log.d(MSG, "================== NO MIC LOCKED ================");
        }
    }

    public void getAmplitude(View view) {
        /**
         *  Function called when Amplitude Button Pressed
         *  purpose: shows current amplitude
         */
        if (mediaRecorder != null) {
//            mediaRecorder
            String y = mediaRecorder.getMaxAmplitude() +"";
//            double x = 20*Math.log10(mediaRecorder.getMaxAmplitude()/600);
//            String x =  + "";

            amplitude.setText(y);
            Log.d(MSG, "===================== Amplitude got is = "+ y);
        }
        else
        {
            Log.d(MSG, "================== NULL ================");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amplitude = (TextView)findViewById(R.id.amp);
    }
}
