package com.example.furniture.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.furniture.R;
import com.example.furniture.models.Products;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;


public class HotProductAdapter extends FirebaseRecyclerAdapter<Products,HotProductAdapter.productsViewholder> {
    private Context context;


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */


    public HotProductAdapter(@NonNull FirebaseRecyclerOptions<Products> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull HotProductAdapter.productsViewholder holder, int position, @NonNull Products model) {
        holder.pName.setText(model.getName());
        holder.pPrice.setText(model.getPrice()+"$");
        Picasso.with(context).load(model.getImage()).into(holder.pImage);


    }

    @NonNull
    @Override
    public HotProductAdapter.productsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_item_view, parent, false);

        context = parent.getContext();
        return new HotProductAdapter.productsViewholder(view);
    }

    public class productsViewholder extends RecyclerView.ViewHolder {
        TextView pName,pPrice;
        ImageView pImage;
        public productsViewholder(@NonNull View itemView) {
            super(itemView);

            pName=itemView.findViewById(R.id.hot_items_prodName);
            pPrice=itemView.findViewById(R.id.hot_items_prodPrice);
            pImage=itemView.findViewById(R.id.hot_items_prodImage);        }
    }
}
