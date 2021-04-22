package com.example.furniture;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class ArViewHolder extends RecyclerView.ViewHolder {
    ImageView prodImage;
    public ArViewHolder(@NonNull View itemView) {
        super(itemView);
        prodImage=itemView.findViewById(R.id.ar_prod_image);
    }
}
