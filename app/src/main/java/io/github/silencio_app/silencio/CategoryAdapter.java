package io.github.silencio_app.silencio;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipin on 29/11/16.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyCategoryViewHolder>{
    private List<String> categoryList;
    private ArrayList<String> colorList;

    public CategoryAdapter(List<String> categoryList, ArrayList<String> colorList) {
        this.categoryList = categoryList;
        this.colorList = colorList;
    }

    public class MyCategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView location_name,location_db;
        public CardView cardView;
        public MyCategoryViewHolder(View view) {
            super(view);
            location_name = (TextView) view.findViewById(R.id.location_name);
            /*location_db = (TextView) view.findViewById(R.id.location_db);*/
            cardView = (CardView) view.findViewById(R.id.card_view);

        }
    }


    @Override
    public MyCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.iiit_location, parent, false);
        return new MyCategoryViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyCategoryViewHolder holder, int position) {
        String category = categoryList.get(position);
        Log.d("DEBUGGER", String.valueOf(holder.cardView));
        Log.d("DEBUGGER", String.valueOf(colorList));
        holder.cardView.setCardBackgroundColor(Color.parseColor(colorList.get(position % colorList.size())));
        holder.location_name.setText(category);
        /*String detail = "Sound level : " + String.valueOf(location.getDb_level()) + " dB";*/
        /*holder.location_db.setText(detail);*/
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}
