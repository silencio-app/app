package io.github.silencio_app.silencio;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        return rootView;
    }
}
