package com.example.ridewise.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridewise.R;
import com.example.ridewise.model.RideOption;

import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    public interface OnRideClickListener {
        void onRideClick(RideOption ride);
    }

    private List<RideOption> rideList;
    private OnRideClickListener listener;

    public RideAdapter(List<RideOption> rideList, OnRideClickListener listener) {
        this.rideList = rideList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ride, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        RideOption ride = rideList.get(position);
        holder.tvProvider.setText(ride.getProvider());
        holder.tvType.setText(ride.getType());
        holder.tvPrice.setText("₹" + ride.getPrice());
        holder.tvEta.setText(ride.getEta());

        holder.itemView.setOnClickListener(v -> listener.onRideClick(ride));
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView tvProvider, tvType, tvPrice, tvEta;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProvider = itemView.findViewById(R.id.tv_provider);
            tvType = itemView.findViewById(R.id.tv_type);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvEta = itemView.findViewById(R.id.tv_eta);
        }
    }
}
