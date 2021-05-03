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

import com.example.furniture.PurchaseActivity;
import com.example.furniture.R;
import com.example.furniture.models.Products;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

public class WishListAdapter extends FirebaseRecyclerAdapter<Products,WishListAdapter.wishListViewHolder> {

    private Context context;
    private String prodID;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public WishListAdapter(@NonNull FirebaseRecyclerOptions<Products> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull WishListAdapter.wishListViewHolder holder, int position, @NonNull Products model) {


        prodID=getRef(position).getKey();

        holder.pName.setText(model.getName());
        //holder.pPrice.setText("$"+model.getPrice());
        Picasso.with(context).load(model.getImage()).into(holder.pImage);

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(v.getContext(), PurchaseActivity.class);
                intent.putExtra("pid",getRef(position).getKey());
                v.getContext().startActivity(intent);
            }
        });



    }

    @NonNull
    @Override
    public WishListAdapter.wishListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wish_list_item, parent, false);

        context = parent.getContext();
        return new WishListAdapter.wishListViewHolder(view);    }

    public class wishListViewHolder extends RecyclerView.ViewHolder {
        TextView pPrice,pName;
        ImageView pImage;
        View v;
        public wishListViewHolder(@NonNull View itemView) {
            super(itemView);
            pName=itemView.findViewById(R.id.wish_list__prodName);
            //pPrice=itemView.findViewById(R.id.wish_list__prodPrice);
            pImage = itemView.findViewById(R.id.wish_list_prodImage);
            v=itemView;




        }
    }
}
