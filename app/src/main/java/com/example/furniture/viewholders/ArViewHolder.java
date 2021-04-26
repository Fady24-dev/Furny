package com.example.furniture.viewholders;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.furniture.R;

public class ArViewHolder extends RecyclerView.ViewHolder {
   public ImageView prodImage;
    public ArViewHolder(@NonNull View itemView) {
        super(itemView);
        prodImage=itemView.findViewById(R.id.ar_prod_image);
    }
}
