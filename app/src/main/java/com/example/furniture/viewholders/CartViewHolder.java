package com.example.furniture.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.furniture.R;

public class CartViewHolder extends RecyclerView.ViewHolder {
    public ImageView img_product;
    public TextView txt_product_name,txt_product_price,txt_poduct_id;
    public Button deleteItem;
    public  View v;
    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        img_product = itemView.findViewById(R.id.img_product);
        txt_product_name = itemView.findViewById(R.id.txt_product_name);
        txt_product_price=itemView.findViewById(R.id.product_price);
        deleteItem=itemView.findViewById(R.id.delete_item_cart_btn);
        txt_poduct_id=itemView.findViewById(R.id.cart_product_id);
        v=itemView;
    }
}
