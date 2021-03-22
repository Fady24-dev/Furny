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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PurchaseActivity extends AppCompatActivity {
    private  Button previewBtn,addCart ;
    private ImageView prodImage;
    private TextView prodName,prodPrice,prodCategory;
    private FirebaseUser user;
    String prodId, userID;
    DatabaseReference Ref;
    Context context;
    String productImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_purchase);

            previewBtn = findViewById(R.id.selected_item_preview_btn);
            addCart = findViewById(R.id.selected_item_add_to_cart_btn);
            prodName = findViewById(R.id.selected_item_name);
            prodPrice = findViewById(R.id.selected_item_price);
            prodCategory = findViewById(R.id.selected_item_category);
            prodImage = findViewById(R.id.selected_item_image);
            user = FirebaseAuth.getInstance().getCurrentUser();

            context=this;


            prodId = getIntent().getStringExtra("pid");

            Ref= FirebaseDatabase.getInstance().getReference().child("Products");
            Ref.child(prodId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String productName=snapshot.child("name").getValue().toString();
                        String productPrice=snapshot.child("price").getValue().toString();
                        String productCategory=snapshot.child("category").getValue().toString();
                        productImage=snapshot.child("image").getValue().toString();

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

            addCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    addToCart();
                }
            });
    }

    private void addToCart() {
        String saveCurrentTime, saveCurrentDate;
        Calendar callForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentDate.format(callForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", prodId);
        cartMap.put("name", prodName.getText().toString());
        cartMap.put("image",productImage);
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("price", prodPrice.getText().toString());

        userID = user.getUid();

        cartListRef.child("User Cart").child(userID).child("Products").child(prodId)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Item has been added to cart", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(context, "Failed to add item", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
