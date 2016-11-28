package io.github.silencio_app.silencio;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by vipin on 28/11/16.
 */
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.MyViewHolder> {

    private List<Location> locationList;

    public LocationAdapter(List<Location> locationList) {
        this.locationList = locationList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView location_name,location_db;

        public MyViewHolder(View view) {
            super(view);
            location_name = (TextView) view.findViewById(R.id.location_name);
            location_db = (TextView) view.findViewById(R.id.location_db);

        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.iiit_location, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Location location = locationList.get(position);

        holder.location_name.setText(location.getName());
        holder.location_db.setText(String.valueOf(location.getDb_level()));
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

}
