package com.example.furniture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.furniture.adapters.HotProductAdapter;
import com.example.furniture.adapters.WishListAdapter;
import com.example.furniture.models.Products;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class WishListActivity extends AppCompatActivity {

    private DatabaseReference Ref;
    private RecyclerView recyclerView;
    private String userId;
    private FirebaseUser user;
    private WishListAdapter wishListAdapter;
    private RelativeLayout emptyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        RelativeLayout emptyLayout = (RelativeLayout) findViewById(R.id.empty_layout);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);



        user= FirebaseAuth.getInstance().getCurrentUser();

        //USER MODE
        if(user != null){
            userId=user.getUid();
            //Firebase RecyclerView
            Ref= FirebaseDatabase.getInstance().getReference().child("Favourite List").child("User List").child(userId).child("Products");
            recyclerView = findViewById(R.id.wish_list_recycler);


            //CHECK IF EMPTY
            Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        emptyLayout.setVisibility(View.INVISIBLE);
                        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

                        // query in the database to fetch appropriate data
                        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                                .setQuery(Ref,Products.class)
                                .build();


                        // Connecting object of required Adapter class to
                        wishListAdapter = new WishListAdapter(options);
                        recyclerView.setAdapter(wishListAdapter);
                        wishListAdapter.startListening();
                    }
                    else{
                        emptyLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        }
        //GUEST MODE
        else {
            emptyLayout.setVisibility(View.VISIBLE);        }
;

    }

    @Override
    protected void onStart() {
        super.onStart();
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
