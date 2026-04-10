package com.example.feldspath;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DonneesViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_data;

    public DonneesViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_data = itemView.findViewById(R.id.tv_data);
    }
}
