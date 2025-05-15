package com.example.balaton;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DistanceAdapter extends RecyclerView.Adapter<DistanceAdapter.ViewHolder> {

    private List<GPSActivity.PlaceWithDistance> data;

    public DistanceAdapter(List<GPSActivity.PlaceWithDistance> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public DistanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DistanceAdapter.ViewHolder holder, int position) {
        GPSActivity.PlaceWithDistance item = data.get(position);
        holder.name.setText(item.name);
        holder.distance.setText(String.format("%.0f m", item.distanceMeters));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, distance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(android.R.id.text1);
            distance = itemView.findViewById(android.R.id.text2);
        }
    }
}
