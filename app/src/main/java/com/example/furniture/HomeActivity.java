package com.example.furniture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.furniture.adapters.HotProductAdapter;
import com.example.furniture.adapters.SearchProductAdapter;
import com.example.furniture.models.Products;
import com.example.furniture.models.Users;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HotProductAdapter hotProductAdapter;
    private DatabaseReference Ref;
    private FirebaseUser user;
    private String userID,searchInput;
    private EditText searchBar;
    private ImageView chairsCategory,sofaHotItem,profileBtn;
    private FloatingActionButton floatingActionButton;
    private int numberOfColumns = 2;
    private LinearLayout chairCatLayout,bedLayout,sofaLayout,closetLayout,officeLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_home);
        searchBar=findViewById(R.id.search_bar_home);
        chairCatLayout = findViewById(R.id.chair_Cat_Selection);
        bedLayout = findViewById(R.id.Bed_Cat_Selection);
        sofaLayout = findViewById(R.id.Sofa_Cat_Selection);
        closetLayout = findViewById(R.id.closet_Cat_Selection);
        officeLayout = findViewById(R.id.Office_Cat_Selection);

        bedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(v.getContext(),CategoryActivity.class);
                intent.putExtra("category","Beds");
                v.getContext().startActivity(intent);
            }
        });


        chairCatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(v.getContext(),CategoryActivity.class);
                intent.putExtra("category","Chairs");
                v.getContext().startActivity(intent);
            }
        });

        sofaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(v.getContext(),CategoryActivity.class);
                intent.putExtra("category","Sofas");
                v.getContext().startActivity(intent);
            }
        });

        closetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(v.getContext(),CategoryActivity.class);
                intent.putExtra("category","Closets");
                v.getContext().startActivity(intent);
            }
        });

        officeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(v.getContext(),CategoryActivity.class);
                intent.putExtra("category","Offices");
                v.getContext().startActivity(intent);
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        Ref=FirebaseDatabase.getInstance().getReference("Users");
        userID=user.getUid();




       Toolbar toolbar = findViewById(R.id.toolbar_home);
       setSupportActionBar(toolbar);



       //Search bar Clicking enter
       searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
           @Override
           public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
               searchInput = searchBar.getText().toString();
               if(TextUtils.isEmpty(searchInput)){
                   return  false;
               }
               else if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                   searchInput=searchInput.substring(0, 1).toUpperCase() + searchInput.substring(1);
                   //do what you want on the press of 'done'
                   Intent intent= new Intent(getApplicationContext(),SearchActivity.class);
                   intent.putExtra("searchInput",searchInput);
                   startActivity(intent);

               }
               return false;
           }
       });

        //Nav Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout1);
        NavigationView navigationView = findViewById(R.id.nav_view1);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_drawer:
                        break;
                    case R.id.my_account_drawer:
                        startActivity(new Intent(getApplicationContext(),Profile_Activity.class));
                        break;
                    case R.id.log_out_drawer:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                        break;
                    case R.id.cart_drawer:
                        startActivity(new Intent(getApplicationContext(),CartActivity.class));
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
        ImageView profileImageView = headerView.findViewById(R.id.header_profile_image_drawer);

        Ref.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                if(user != null){
                    String username= user.getName();
                    String email= user.getEmail();
                    String image = user.getImage();

                    userNameTextView.setText(username);
                    userEmailTextView.setText(email);
                    Picasso.with(getApplicationContext()).load(user.getImage()).into(profileImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Firebase RecyclerView
        Ref= FirebaseDatabase.getInstance().getReference().child("Products");
        recyclerView = findViewById(R.id.hot_items_recyclerview);

        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2 ));

        // query in the database to fetch appropriate data
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(Ref,Products.class)
                .build();

        // Connecting object of required Adapter class to
        hotProductAdapter = new HotProductAdapter(options);
        recyclerView.setAdapter(hotProductAdapter);

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
