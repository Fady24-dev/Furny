package com.example.furniture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.furniture.models.Products;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class PurchaseActivity extends AppCompatActivity {
    private  Button previewBtn,addCart ;
    private ImageButton fvrtBtn;
    private ImageView prodImage;
    private TextView prodName,prodPrice,prodCategory;
    private FirebaseUser user;
    private String prodId, userID;
    private DatabaseReference Ref;
    private Context context;
    private String productImage,prodModel ,productPrice;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_purchase);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            previewBtn = findViewById(R.id.selected_item_preview_btn);
            fvrtBtn=findViewById(R.id.fav_purchase_btn);
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
                        productPrice =snapshot.child("price").getValue().toString();
                        String productCategory=snapshot.child("category").getValue().toString();
                        productImage=snapshot.child("image").getValue().toString();
                        prodModel=snapshot.child("model").getValue().toString();



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
                    Intent intent = new Intent(getApplicationContext(),ArActivity.class);
                    intent.putExtra("pid",prodId);
                    intent.putExtra("activity","purchase");
                    startActivity(intent);

//                    final DatabaseReference prevRef = FirebaseDatabase.getInstance().getReference().child("Preview List").child("User List").child(userID).child("Products");
//                    prevRef.removeValue();
                    //addToPreview();
                }
            });


            addCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(user != null){
                        addToCart();
                    }
                    else {
                        Toast.makeText(context, "Log in first!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if(user != null){
                userID=user.getUid();
                fvrtBtn.setVisibility(View.VISIBLE);
                fvrtBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final HashMap<String, Object> favMap = new HashMap<>();
                        favMap.put("pid",prodId);
                        favMap.put("name",prodName.getText().toString());
                        favMap.put("model",prodModel);
                        favMap.put("image",productImage);
                        favMap.put("category",prodCategory.toString());
                        favMap.put("price", productPrice);
                        //fvrtBtn.setImageResource(R.drawable.ic_favorite_red_24dp);

                        final DatabaseReference favRef = FirebaseDatabase.getInstance().getReference().child("Favourite List").child("User List").child(userID)
                                .child("Products");
                        favRef.child(prodId).updateChildren(favMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(context, "Added To Wishlist", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                });
            }

    }

    private void addToCart() {
        String saveCurrentTime, saveCurrentDate;
        Calendar callForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentDate.format(callForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User Cart");

        //Hash map to add values in "Products in Cart List"
        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", prodId);
        cartMap.put("name", prodName.getText().toString());
        cartMap.put("model", prodModel);
        cartMap.put("image",productImage);
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("price", productPrice);

        userID = user.getUid();

        cartListRef.child(userID).child("Products").child(prodId)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Added To Cart", Toast.LENGTH_SHORT).show();

                        }
                        else {
                            Toast.makeText(context, "Failed to add item", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

//    private void addToPreview() {
//
//        final DatabaseReference previewRef = FirebaseDatabase.getInstance().getReference().child("Preview List");
//
//        final HashMap<String, Object> cartMap = new HashMap<>();
//        cartMap.put("pid", prodId);
//        cartMap.put("name", prodName.getText().toString());
//        cartMap.put("model", prodModel);
//        cartMap.put("image",productImage);
//        userID = user.getUid();
//
//        previewRef.child("User List").child(userID).child("Products").child(prodId)
//                .updateChildren(cartMap)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                           startActivity(new Intent(getApplicationContext(),ArActivity.class));
//                        }
//                        else {
//                        }
//                    }
//                });
//    }
}
