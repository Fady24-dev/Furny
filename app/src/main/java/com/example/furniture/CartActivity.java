package com.example.furniture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.furniture.models.Products;
import com.example.furniture.viewholders.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    DatabaseReference Ref,priceRef;
    RecyclerView recyclerView;
    private FirebaseUser user;
    private Button previewBtn;
    private TextView totalPrice,emptyTxt;
    private String prodID,elegantCounter;
    private int overTotalPrice,quantity;

    // private int overTotalPrice;


    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_cart);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        previewBtn=findViewById(R.id.preview_allcart_btn);
        totalPrice=findViewById(R.id.total_cart_price);
        emptyTxt=findViewById(R.id.empty_recycler_cart);


        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        //Firebase RecyclerView
        Ref= FirebaseDatabase.getInstance().getReference().child("Cart List").child("User Cart").child(userID).child("Products");
        recyclerView = findViewById(R.id.recycler_cart);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        totalPrice.setText(String.valueOf(overTotalPrice));
        quantity=1;


        previewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),ArActivity.class);
                intent.putExtra("cartActivity","cart");
                startActivity(intent);
//                Ref.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Ref = FirebaseDatabase.getInstance().getReference().child("Preview List").child("User List").child(userID).child("Products");
//                        final HashMap<String, Object> previewMap = new HashMap<>();
//                        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
//                            Products products = postSnapshot.getValue(Products.class);
//                            previewMap.put("pid", products.getPid());
//                            previewMap.put("name", products.getName());
//                            previewMap.put("model", products.getModel());
//                            previewMap.put("image",products.getImage());
//
//                            Ref.child(products.getPid()).updateChildren(previewMap);
//                            Toast.makeText(CartActivity.this, "Added", Toast.LENGTH_SHORT).show();
//                        }
//                        previewMap.clear();
//                        startActivity(new Intent(getApplicationContext(),ArActivity.class));
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

            }
        });




    }
    //To Save data when coming back to activity
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

        savedInstanceState.putBoolean("MyBoolean", true);
        savedInstanceState.putDouble("myDouble", 1.9);
        savedInstanceState.putInt("MyInt", 1);
        savedInstanceState.putString("MyString", "Welcome back to Android");


        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }



    @Override
    protected void onStart() {
        super.onStart();

        //To Preview RecyclerView
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(Ref,Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, CartViewHolder> adapter= new FirebaseRecyclerAdapter<Products, CartViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Products model) {



                // Picasso.with(context).load(model.getImage()).into(holder.img_product);
                holder.txt_product_name.setText(model.getName());
                Picasso.with(getApplicationContext()).load(model.getImage()).into(holder.img_product);
                holder.txt_product_price.setText("$"+model.getPrice());
                holder.txt_poduct_id.setText(model.getPid());

                // to count the total price in cart
                    int oneProductPrice = Integer.parseInt(model.getPrice());
                    //int oneProductQuantity = Integer.parseInt(holder.countButton.getNumber());
                    int oneTyprProductTPrice = oneProductPrice*quantity;
                    overTotalPrice = overTotalPrice + oneTyprProductTPrice;
                    totalPrice.setText("$"+String.valueOf(overTotalPrice));




                holder.countButton.setOnClickListener(new ElegantNumberButton.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.countButton.setNumber( holder.countButton.getNumber());
                        quantity = Integer.parseInt(holder.countButton.getNumber());
                        overTotalPrice = overTotalPrice + oneTyprProductTPrice;
                        totalPrice.setText("$"+String.valueOf(overTotalPrice));

                        prodID =getRef(holder.getAdapterPosition()).getKey();
                        Toast.makeText(CartActivity.this, holder.countButton.getNumber(), Toast.LENGTH_SHORT).show();
                    }
                });

                //priceRef =FirebaseDatabase.getInstance().getReference().child("Cart List").child("User Cart").child(userID);
                //priceRef.child("Total Price").setValue(overTotalPrice);
                holder.deleteItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Don't ever use position inside of listeners
                        quantity = Integer.parseInt(holder.countButton.getNumber());
                        prodID =getRef(holder.getAdapterPosition()).getKey();
                        Ref= FirebaseDatabase.getInstance().getReference().child("Cart List").child("User Cart").child(userID).child("Products").child(prodID);
                        overTotalPrice=overTotalPrice-(oneProductPrice*quantity);
                        totalPrice.setText("$"+String.valueOf(overTotalPrice));
                        Ref.removeValue();
                        Toast.makeText(CartActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();
                    }
                });

            }


            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        //Display RecyclerView
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
