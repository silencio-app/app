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

    public void getstart(View view){
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

    public void stop(View view) {
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
        if (mediaRecorder != null) {
//            mediaRecorder
            String y = mediaRecorder.getMaxAmplitude() +"";
            double x = 20*Math.log10(mediaRecorder.getMaxAmplitude()/600);
//            String x =  + "";

            amplitude.setText(y);
            Log.d(MSG, "======================"+ y+"========="+x);
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
