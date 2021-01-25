package com.example.furniture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.ar.sceneform.ux.ArFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PurchaseActivity extends AppCompatActivity {
    private  Button previewBtn,buyBtn ;
    private ImageView prodImage;
    private TextView prodName,prodPrice,prodCategory;
    DatabaseReference Ref;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_purchase);

            previewBtn = findViewById(R.id.selected_item_preview_btn);
            prodName=findViewById(R.id.selected_item_name);
            prodPrice=findViewById(R.id.selected_item_price);
            prodCategory=findViewById(R.id.selected_item_category);
            prodImage=findViewById(R.id.selected_item_image);

            context=this;


            String prodId=getIntent().getStringExtra("pid");

            Ref= FirebaseDatabase.getInstance().getReference().child("Products");
            Ref.child(prodId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String productName=snapshot.child("name").getValue().toString();
                        String productPrice=snapshot.child("price").getValue().toString();
                        String productCategory=snapshot.child("category").getValue().toString();
                        String productImage=snapshot.child("image").getValue().toString();

                        Picasso.with(context).load(productImage).into(prodImage);
                        prodName.setText(productName);
                        prodPrice.setText("$"+productPrice);
                        prodCategory.setText(productCategory);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            previewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PurchaseActivity.this, ArActivity.class);
                    intent.putExtra("id",prodId);
                    startActivity(intent);
                }
            });
    }
}
