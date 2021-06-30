package com.example.furniture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.furniture.adapters.HotProductAdapter;
import com.example.furniture.adapters.SearchProductAdapter;
import com.example.furniture.models.Products;
import com.example.furniture.models.Users;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SearchActivity extends AppCompatActivity {
    private EditText searchBar;
    private String receivedSearchedWord,userID;
    private RecyclerView recyclerView;
    private DatabaseReference Ref,userRef;
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

        user = FirebaseAuth.getInstance().getCurrentUser();
        userRef=FirebaseDatabase.getInstance().getReference("Users");




        recyclerView=findViewById(R.id.search_items_recyclerview);


        Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

        //Nav Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout2);
        NavigationView navigationView = findViewById(R.id.nav_view2);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_drawer:
                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        finish();
                        break;
                    case R.id.my_account_drawer:
                        startActivity(new Intent(getApplicationContext(),Profile_Activity.class));
                        break;
                    case R.id.cart_drawer:
                        startActivity(new Intent(getApplicationContext(),CartActivity.class));
                        break;
                    case R.id.log_out_drawer:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        Toast.makeText(SearchActivity.this, "Logged Out Successfully.", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                }
                drawer.closeDrawer((GravityCompat.START));
                return true;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigtaion_drawer_open, R.string.navigtaion_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_name_profile_drawer);
        TextView userEmailTextView = headerView.findViewById(R.id.email_user_drawer);
        TextView signupGuest = headerView.findViewById(R.id.signin_signup_btn);
        ImageView profileImageView = headerView.findViewById(R.id.header_profile_image_drawer);


        //USER MODE
        if(user != null){
            userID =user.getUid();
            profileImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(),Profile_Activity.class));
                }
            });
            userRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users user = snapshot.getValue(Users.class);
                    if(user != null){
                        String username= user.getName();
                        String email= user.getEmail();
                        String image = user.getImage();

                        userEmailTextView.setVisibility(View.VISIBLE);
                        signupGuest.setVisibility(View.INVISIBLE);
                        userNameTextView.setText(username);
                        userEmailTextView.setText(email);
                        Picasso.with(getApplicationContext()).load(user.getImage()).into(profileImageView);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        //Guest MODE
        else {
            userEmailTextView.setVisibility(View.INVISIBLE);
            signupGuest.setVisibility(View.VISIBLE);
            signupGuest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
            });
        }



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
            return;
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
