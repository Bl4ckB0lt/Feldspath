package com.example.feldspath;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DonneesViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_date;
    public TextView tv_gaz;
    public TextView tv_humidite;
    public TextView tv_temperature;


    public DonneesViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_date = itemView.findViewById(R.id.TVdate);
        tv_gaz = itemView.findViewById(R.id.TVgaz);
        tv_humidite = itemView.findViewById(R.id.TVhumidite);
        tv_temperature = itemView.findViewById(R.id.TVtemperature);
    }
}
