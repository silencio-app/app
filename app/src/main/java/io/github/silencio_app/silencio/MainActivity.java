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
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private MediaRecorder mediaRecorder = null;
    private String MSG = "MIC";
    private TextView amplitude;
    private boolean recording_flag = false;
    private LineGraphSeries<DataPoint> series;
    private static final Random RANDOM = new Random();
    private int lastX = 0;

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
        series = new LineGraphSeries<>();
        graph.addSeries(series);
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(32768);
        viewport.setMaxX(10);
        viewport.setScalable(true);
    }
//    @Override
//    protected void onResume(){
//        super.onResume();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i=0;i<100;i++){
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            addEntry();
//                        }
//                    });
//                    try {
//                        Thread.sleep(600);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//    }
    private void addEntry() {
        series.appendData(new DataPoint(lastX++, RANDOM.nextDouble()*10d), true, 10);
    }

    private class AudioListener implements Runnable{
        public int getAmplitude() {
            if (mediaRecorder != null) {
                int y = mediaRecorder.getMaxAmplitude();
                return y;
            }
            else
            {
                return -1;
            }
        }
        private String MSG = "AudioListener Class LOG: ";
        /*

         */
        @Override
        public void run(){
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            while(recording_flag){
                final int y = getAmplitude();
                final String x = y+"";
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        amplitude.setText(x);
                        series.appendData(new DataPoint(lastX++, y), true, 32768);
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
