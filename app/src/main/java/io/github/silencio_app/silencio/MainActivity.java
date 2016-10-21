package io.github.silencio_app.silencio;

import android.media.MediaRecorder;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private MediaRecorder mediaRecorder = null;
    private String MSG = "MIC";
    private TextView amplitude;
    private boolean recording_flag = false;

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
                recording_flag = true;
                Thread newT = new Thread(new AudioListener());
                newT.start();

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
            recording_flag = false;
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
        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3)
        });
        graph.addSeries(series);
    }

    private class AudioListener implements Runnable{
        public String getAmplitude() {
            if (mediaRecorder != null) {
                String y = mediaRecorder.getMaxAmplitude() +"";
                return y;
            }
            else
            {
                return null;
            }
        }
        private String MSG = "AudioListener Class LOG: ";
        /*

         */
        @Override
        public void run(){
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            while(recording_flag){
                final String y = getAmplitude();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        amplitude.setText(y);
                    }
                });
                Log.d(MSG, " ========  got amplitude "+ y);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
