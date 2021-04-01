package com.example.furniture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.furniture.adapters.CartAdapter;
import com.example.furniture.adapters.HotProductAdapter;
import com.example.furniture.models.Products;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    DatabaseReference Ref;
    RecyclerView recyclerView;
    private CartAdapter CartAdapter;
    private FirebaseUser user;
    private Button previewBtn;
    private TextView totalPrice;
    List<Products> productsList = new ArrayList<>();

    String userID,prodId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        previewBtn=findViewById(R.id.preview_allcart_btn);
        totalPrice=findViewById(R.id.total_cart_price);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        //Firebase RecyclerView
        Ref= FirebaseDatabase.getInstance().getReference().child("Cart List").child("User Cart").child(userID).child("Products");
        recyclerView = findViewById(R.id.recycler_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // query in the database to fetch appropriate data
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(Ref,Products.class)
                .build();

        // Connecting object of required Adapter class to
        CartAdapter = new CartAdapter(options);
        recyclerView.setAdapter(CartAdapter);


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



    @Override
    protected void onStart() {
        super.onStart();
        CartAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        CartAdapter.stopListening();
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
