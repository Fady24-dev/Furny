package com.example.furniture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.example.furniture.adapters.CartAdapter;
import com.example.furniture.adapters.HotProductAdapter;
import com.example.furniture.models.Products;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CartActivity extends AppCompatActivity {

    DatabaseReference Ref;
    RecyclerView recyclerView;
    private CartAdapter CartAdapter;
    private FirebaseUser user;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

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
