package com.example.furniture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.furniture.adapters.SearchProductAdapter;
import com.example.furniture.models.Products;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminViewActivity extends AppCompatActivity {
    private EditText searchBar;
    private String receivedSearchedWord;
    private RecyclerView recyclerView;
    private DatabaseReference Ref;
    private SearchProductAdapter searchProductAdapter;
    private FirebaseUser user;

    private int numberOfColumns = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_search);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        searchBar=findViewById(R.id.search_bar_main);
        receivedSearchedWord=getIntent().getStringExtra("searchInput");
        searchBar.setText(receivedSearchedWord);


        recyclerView=findViewById(R.id.search_items_recyclerview);

        user = FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

        //Nav Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout2);
        NavigationView navigationView = findViewById(R.id.nav_view2);

        //Admin Mode
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.admin_menu);
        if(user != null) {
            if(user.getEmail().equals("admin0000@mail.com")) {
                navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.add_products:
                                startActivity(new Intent(getApplicationContext(), AddProductActivity.class));
                                break;

                            case R.id.log_out_drawer_admin:
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                                Toast.makeText(AdminViewActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                                finish();
                                break;
                        }
                        drawer.closeDrawer((GravityCompat.START));
                        return true;
                    }
                });
            }
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigtaion_drawer_open, R.string.navigtaion_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        //Search bar
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            String searchInput = searchBar.getText().toString();
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    onStart();
                }
                return false;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Firebase RecyclerView
        String searchInput = searchBar.getText().toString();
        if(TextUtils.isEmpty(searchInput)){
            Ref= FirebaseDatabase.getInstance().getReference().child("Products");
            recyclerView = findViewById(R.id.search_items_recyclerview);

            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),numberOfColumns));

            // query in the database to fetch appropriate data
            FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                    .setQuery(Ref,Products.class)
                    .build();


            // Connecting object of required Adapter class to
            searchProductAdapter= new SearchProductAdapter(options);
            recyclerView.setAdapter(searchProductAdapter);
            searchProductAdapter.startListening();
        }
        else {
            searchInput=searchInput.substring(0, 1).toUpperCase() + searchInput.substring(1);
            Ref= FirebaseDatabase.getInstance().getReference().child("Products");
            recyclerView = findViewById(R.id.search_items_recyclerview);

            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),numberOfColumns));

            // query in the database to fetch appropriate data
            FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                    .setQuery(Ref.orderByChild("name").startAt(searchInput).endAt(searchInput+"\uf8ff"),Products.class)
                    .build();


            // Connecting object of required Adapter class to
            searchProductAdapter= new SearchProductAdapter(options);
            recyclerView.setAdapter(searchProductAdapter);
            searchProductAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        searchProductAdapter.startListening();
    }
}

