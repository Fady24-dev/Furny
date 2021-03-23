package com.example.furniture.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.furniture.HomeActivity;
import com.example.furniture.PurchaseActivity;
import com.example.furniture.R;
import com.example.furniture.models.Products;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class CartAdapter extends FirebaseRecyclerAdapter<Products, CartAdapter.CartViewHolder> {
    private Context context;
    private DatabaseReference Ref;
    private FirebaseUser user;
    private String userID,prodID;
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

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();


      holder.deleteItem.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              //Don't ever use position inside of listeners
              prodID =getRef(holder.getAdapterPosition()).getKey();
              Ref= FirebaseDatabase.getInstance().getReference().child("Cart List").child("User Cart").child(userID).child("Products").child(prodID);
              Ref.removeValue();
          }
      });


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
        View v;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

           img_product = itemView.findViewById(R.id.img_product);
           txt_product_name = itemView.findViewById(R.id.txt_product_name);
            txt_product_price=itemView.findViewById(R.id.product_price);
            deleteItem=itemView.findViewById(R.id.delete_item_cart_btn);
            v=itemView;
        }
    }
}
