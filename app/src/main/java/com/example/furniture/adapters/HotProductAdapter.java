package com.example.furniture.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.furniture.PurchaseActivity;
import com.example.furniture.R;
import com.example.furniture.interfaces.ItemClickListner;
import com.example.furniture.models.Products;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class HotProductAdapter extends FirebaseRecyclerAdapter<Products,HotProductAdapter.productsViewholder> {
    private Context context;
    private DatabaseReference Ref;
    private FirebaseUser user;
    private String prodID,userID;


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

        prodID=getRef(position).getKey();

        holder.pName.setText(model.getName());
        holder.pPrice.setText("$"+model.getPrice());
        Picasso.with(context).load(model.getImage()).into(holder.pImage);

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(v.getContext(),PurchaseActivity.class);
                intent.putExtra("pid",getRef(position).getKey());
                v.getContext().startActivity(intent);
            }
        });

        holder.fvrtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DatabaseReference favRef = FirebaseDatabase.getInstance().getReference().child("Favourite List");
                String prodId=getRef(position).getKey();
                final HashMap<String, Object> favMap = new HashMap<>();
                favMap.put("pid", model.getPid());
                favMap.put("name", model.getName());
                favMap.put("model", model.getModel());
                favMap.put("image",model.getImage());
                favMap.put("category", model.getCategory());
                favMap.put("price", model.getPrice());
                user= FirebaseAuth.getInstance().getCurrentUser();
                userID = user.getUid();
                //holder.fvrtBtn.setImageResource(R.drawable.ic_favorite_red_24dp);


                favRef.child("User List").child(userID).child("Products").child(prodId)
                .updateChildren(favMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Added To Wishlist", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(context, "Something went Wrong", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

    }

    @NonNull
    @Override
    public HotProductAdapter.productsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_item_view, parent, false);

        context = parent.getContext();
        return new HotProductAdapter.productsViewholder(view);
    }

    public static class productsViewholder extends RecyclerView.ViewHolder{
       public TextView pName, pPrice;
        public ImageView pImage;
        public ImageButton fvrtBtn;
        public  View v;
        public ItemClickListner listner;

        public productsViewholder(@NonNull View itemView) {
            super(itemView);
            pName = itemView.findViewById(R.id.hot_items_prodName);
            pPrice = itemView.findViewById(R.id.hot_items_prodPrice);
            pImage = itemView.findViewById(R.id.hot_items_prodImage);
            fvrtBtn=itemView.findViewById(R.id.fav_btn);
            v=itemView;
        }
    }



}
