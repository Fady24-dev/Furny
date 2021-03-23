package com.example.furniture.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.furniture.R;
import com.example.furniture.models.Products;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;


public class CartAdapter extends FirebaseRecyclerAdapter<Products, CartAdapter.CartViewHolder> {
    private Context context;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CartAdapter(@NonNull FirebaseRecyclerOptions<Products> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Products model) {
        // Picasso.with(context).load(model.getImage()).into(holder.img_product);
        holder.txt_product_name.setText(model.getName());
        Picasso.with(context).load(model.getImage()).into(holder.img_product);
        holder.txt_product_price.setText(model.getPrice());

    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item_layout, parent, false);

        context = parent.getContext();
        return new CartAdapter.CartViewHolder(view);
    }

    class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView img_product;
        TextView txt_product_name,txt_product_price;
        Button deleteItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

           img_product = itemView.findViewById(R.id.img_product);
           txt_product_name = itemView.findViewById(R.id.txt_product_name);
            txt_product_price=itemView.findViewById(R.id.product_price);
            deleteItem=itemView.findViewById(R.id.delete_item_cart_btn);



        }
    }
}
