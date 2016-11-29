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
import com.jjoe64.graphview.GridLabelRenderer;
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

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        graph_two.addSeries(series);
        Viewport viewport = graph_two.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMinY(0);  // min value is 0
        viewport.setMaxY(100);  // max value is 32768
        viewport.setMaxX(100);  // 10 units frame
        GridLabelRenderer gridLabel = graph_two.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Intervals");
        gridLabel.setVerticalAxisTitle("db Levels");

        /*series.appendData(new DataPoint(lastX++, amp_val), true, 100);*/

        if(records.length < 1){
            series.appendData(new DataPoint(0, 0), true, 100);
        }
        else{
            for(int i=0;i<records.length;i++){
                Log.d("UAHSDASDJ", String.valueOf(records[i]));
                series.appendData(new DataPoint(i, records[i]), true, 100);
            }
        }
        return rootView;
    }
}
