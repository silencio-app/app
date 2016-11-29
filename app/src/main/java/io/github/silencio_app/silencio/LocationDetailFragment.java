package io.github.silencio_app.silencio;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by vipin on 29/11/16.
 */
public class LocationDetailFragment extends Fragment {
    private TextView vp_location_name, vp_location_db, vp_location_mac;
    private Bundle passedData;

    public LocationDetailFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passedData = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.location_detail_fragment, container, false);

        vp_location_name = (TextView) rootView.findViewById(R.id.vp_location_name);
        vp_location_db = (TextView) rootView.findViewById(R.id.vp_location_db);

        vp_location_name.setText(passedData.getString("location_name"));
        vp_location_db.setText(passedData.getString("location_db"));
        Log.d("ASMKBDSABDA", String.valueOf(passedData.getFloatArray("location_records")));

        GraphView graph_two = (GraphView) rootView.findViewById(R.id.graph_two);
        Log.d("GRAPH", String.valueOf(graph_two));
        float [] records = passedData.getFloatArray("location_records");
        DataPoint[] dataPoints = new DataPoint[records.length+1];
        if(records.length < 1){
            dataPoints[0] = new DataPoint(0,0);
        }
        else{
            for(int i=0;i<records.length;i++){
                dataPoints[i] = new DataPoint(i, records[i]);
            }
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3)
        });
        /*Log.d("GRAPH", String.valueOf(series[0]));*/
        graph_two.addSeries(series);
        Viewport viewport = graph_two.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinX(1.0);
        viewport.setMinY(0);  // min value is 0
        viewport.setMaxY(100);  // max value is 32768
        viewport.setMaxX(100);  // 10 units frame
        viewport.setScalable(true); // auto scroll to right

        return rootView;
    }
}
