package com.example.furniture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    ImageView chairsCategory,sofaHotItem,profileBtn;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        chairsCategory = findViewById(R.id.chairs_category);
        sofaHotItem = findViewById(R.id.sofa_hot_item);
        floatingActionButton=findViewById(R.id.favorite);
        profileBtn=findViewById(R.id.profile_btn);

        profileBtn.setOnClickListener(new View.OnClickListener() {
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
        });




        chairsCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,PurchaseActivity.class);
                startActivity(intent);
            }
        });

    }
}
