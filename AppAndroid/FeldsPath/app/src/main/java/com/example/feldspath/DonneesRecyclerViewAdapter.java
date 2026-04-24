package com.example.feldspath;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DonneesRecyclerViewAdapter extends RecyclerView.Adapter<DonneesViewHolder> {
    public ArrayList<DonneesCapteur> lstData;

    public void setLstData(ArrayList<DonneesCapteur> lstData) {
        this.lstData = lstData;
        notifyDataSetChanged();
    }

    public DonneesRecyclerViewAdapter() {
        lstData = new ArrayList<>();
    }

    @NonNull
    @Override
    public DonneesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_affichagedata, parent, false);
        return new DonneesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonneesViewHolder holder, int position) {
        DonneesCapteur uneData = lstData.get(position);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault()
        );
        String dateFormatee = sdf.format(new java.util.Date(uneData.getDate()));

        holder.tv_date.setText(dateFormatee + "");
        holder.tv_gaz.setText(String.format(uneData.getGaz() + ""));
        holder.tv_humidite.setText(String.format(uneData.getHumidite() + ""));
        holder.tv_temperature.setText(String.format(uneData.getTemperature() + ""));

    }

    @Override
    public int getItemCount() {
        return lstData.size();
    }
}
