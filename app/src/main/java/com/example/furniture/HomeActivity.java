package com.example.furniture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.example.furniture.adapters.HotProductAdapter;
import com.example.furniture.models.Products;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HotProductAdapter hotProductAdapter;
    private DatabaseReference Ref;
    private ImageView chairsCategory,sofaHotItem,profileBtn;
    private FloatingActionButton floatingActionButton;
    private int numberOfColumns = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_home);

       Toolbar toolbar = findViewById(R.id.toolbar_home);
       setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout1);
        NavigationView navigationView = findViewById(R.id.nav_view1);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigtaion_drawer_open, R.string.navigtaion_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();






        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.profile_menu:
                        startActivity(new Intent(getApplicationContext(),Profile_Activity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home_menu:
                        return true;
                }
                return false;
            }
        });
       // chairsCategory = findViewById(R.id.chairs_category);
        //sofaHotItem = findViewById(R.id.sofa_hot_item);
       //floatingActionButton=findViewById(R.id.favorite);
       // profileBtn=findViewById(R.id.profile_btn);

        Ref= FirebaseDatabase.getInstance().getReference().child("Products");
        recyclerView = findViewById(R.id.hot_items_recyclerview);

        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        // query in the database to fetch appropriate data
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(Ref,Products.class)
                .build();

        // Connecting object of required Adapter class to
        hotProductAdapter = new HotProductAdapter(options);
        recyclerView.setAdapter(hotProductAdapter);


        /*profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Profile_Activity.class));
            }
        });

        //Sign out
         floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
                Toast.makeText(HomeActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
            }

        }); */




       /* chairsCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,PurchaseActivity.class);
                startActivity(intent);
            }
        }); */


    }


    //Getting data from database on starting of the activity
    @Override
    protected void onStart() {
        super.onStart();
        hotProductAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hotProductAdapter.stopListening();
    }

}
