package com.example.furniture;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.furniture.models.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminProductViewActivity extends AppCompatActivity {
    private ImageView prodImage;
    private TextView prodName,prodPrice,prodCategory;
    private EditText itemName, itemPrice, itemDesc;
    private String prodId;
    private Context context;
    private DatabaseReference Ref;
    private StorageReference storageReference;
    private String productImage,productPrice, editFabState;
    private FloatingActionButton adminEditFab, adminDeleteFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_view);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Ref= FirebaseDatabase.getInstance().getReference().child("Products");
        storageReference = FirebaseStorage.getInstance().getReference();
        context = this;

        prodName = findViewById(R.id.selected_item_name);
        prodPrice = findViewById(R.id.selected_item_price);
        prodCategory = findViewById(R.id.selected_item_category);
        prodImage = findViewById(R.id.selected_item_image);

        itemName = findViewById(R.id.selected_item_name);
        itemPrice = findViewById(R.id.selected_item_price);
        itemDesc = findViewById(R.id.selected_item_description);


        prodId = getIntent().getStringExtra("pid");

        adminEditFab = findViewById(R.id.admin_edit_fab);
        adminDeleteFab = findViewById(R.id.admin_delete_fab);
        editFabState = "disabled";

        getCurrentProductsData();

        adminEditFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editFabState == "disabled"){

                    itemName.setEnabled(true);
                    itemPrice.setEnabled(true);
                    itemDesc.setEnabled(true);

                    adminEditFab.setImageResource(R.drawable.check_mark_white);
                    editFabState = "enabled";

                    readData();
                }

                else if(editFabState == "enabled"){

                    itemName.setEnabled(false);
                    itemPrice.setEnabled(false);
                    itemDesc.setEnabled(false);

                    adminEditFab.setImageResource(R.drawable.edit_pen_white);
                    editFabState = "disabled";

                    submitData();
                }
            }
        });

        adminDeleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(AdminProductViewActivity.this);

                deleteDialog.setMessage("Are you sure you want to delete this model?");

                deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Ref.child(prodId).removeValue();
                        startActivity(new Intent(getApplicationContext(),AdminViewActivity.class));
                        Toast.makeText(context, "Model deleted", Toast.LENGTH_SHORT).show();
                    }
                });

                deleteDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                deleteDialog.show();
            }
        });


        prodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 1000);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri productImageUri = data.getData();

                uploadImage(productImageUri);
            }
        }
    }

    private void uploadImage(Uri productImageUri) {
        StorageReference imageRef = storageReference.child("Product Images/"+prodId+"/product.jpg");
        imageRef.putFile(productImageUri);

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Ref.child(prodId).child("image").setValue(uri.toString());
            }
        });
    }

    private void readData() {
        Ref.child(prodId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    Products product = snapshot.getValue(Products.class);

                    itemName.setText(product.getName());
                    itemPrice.setText(product.getPrice());
                    itemDesc.setText(product.getDescription());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void submitData() {

        final HashMap<String, Object> profileMap = new HashMap<>();
        profileMap.put("name",itemName.getText().toString());
        profileMap.put("price",itemPrice.getText().toString());
        profileMap.put("description",itemDesc.getText().toString());

        Ref.child(prodId).updateChildren(profileMap);

        getCurrentProductsData();
    }

    private void getCurrentProductsData() {
        Ref.child(prodId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Products product = snapshot.getValue(Products.class);

                if (product != null) {

                    String name = product.getName();
                    String price = product.getPrice();
                    String desc = product.getDescription();
                    String image = product.getImage();


                    itemName.setText(name);
                    itemPrice.setText("$"+price);
                    itemDesc.setText(desc);

                    Picasso.with(getApplicationContext()).load(image).into(prodImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
