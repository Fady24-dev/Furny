package com.example.furniture.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.furniture.AdminProductViewActivity;
import com.example.furniture.PurchaseActivity;
import com.example.furniture.R;
import com.example.furniture.interfaces.ItemClickListner;
import com.example.furniture.models.Products;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

public class SearchProductAdapter extends FirebaseRecyclerAdapter<Products,SearchProductAdapter.productsViewholder> {
    private Context context;


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public SearchProductAdapter(@NonNull FirebaseRecyclerOptions<Products> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SearchProductAdapter.productsViewholder holder, int position, @NonNull Products model) {
        holder.pName.setText(model.getName());
        holder.pPrice.setText("$"+model.getPrice());
        Picasso.with(context).load(model.getImage()).into(holder.pImage);
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String className = v.getContext().getClass().getSimpleName();

                if (className.equals("HomeActivity") || className.equals("SearchActivity")) {
                    Intent intent= new Intent(v.getContext(),PurchaseActivity.class);
                    intent.putExtra("pid",getRef(position).getKey());
                    v.getContext().startActivity(intent);
                }

                else if (className.equals("AdminViewActivity")) {
                    Intent intent= new Intent(v.getContext(), AdminProductViewActivity.class);
                    intent.putExtra("pid",getRef(position).getKey());
                    v.getContext().startActivity(intent);
                }
            }
        });
    }

    @NonNull
    @Override
    public SearchProductAdapter.productsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_item_view, parent, false);

        context = parent.getContext();
        return new SearchProductAdapter.productsViewholder(view);
    }

    public class productsViewholder extends RecyclerView.ViewHolder {
        TextView pName, pPrice;
        ImageView pImage;
        View v;
        public ItemClickListner listner;
        public productsViewholder(@NonNull View itemView) {
            super(itemView);
            pName = itemView.findViewById(R.id.hot_items_prodName);
            pPrice = itemView.findViewById(R.id.hot_items_prodPrice);
            pImage = itemView.findViewById(R.id.hot_items_prodImage);
            v=itemView;

        }
    }
}
