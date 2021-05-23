package com.example.furniture;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class AddProductActivity extends AppCompatActivity  {

    private  ImageView mImageView;
    private String category,CategoryName, Description, Price, Pname, saveCurrentDate, saveCurrentTime,productRandomKey, downloadImageUrl,downloadFileUrl;
    private Button AddNewProductButton;
    private ImageButton uploadFile;
    private ImageView InputProductImage;
    private EditText InputProductName, InputProductCateogyr, InputProductPrice;
    private static final int GalleryPick = 1;
    private StorageReference ProductImagesRef,productFileRef;
    private DatabaseReference ProductsRef;
    private ProgressDialog loadingBar;
    private FloatingActionButton logout;
    private static final int IMAGE_PICK_CODE =1000;
    private static final int PICKFILE_REQUEST_CODE =1001;
    private Uri imageUri,fileUri ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_add_product);
        //VIEWS
        mImageView = findViewById(R.id.image_view);
        uploadFile = findViewById(R.id.choose_btn);
        AddNewProductButton = findViewById(R.id.choose_image_btn);
        InputProductImage = findViewById(R.id.image_view);
        InputProductName = findViewById(R.id.add_product_name);
        InputProductCateogyr = findViewById(R.id.add_product_category);
        InputProductPrice = findViewById(R.id.add_product_price);
        logout=findViewById(R.id.log_out_admin);

        loadingBar = new ProgressDialog(this);

        //handle button click

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                Toast.makeText(AddProductActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        //CategoryName = getIntent().getExtras().get("category").toString();
        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productFileRef= FirebaseStorage.getInstance().getReference().child("3Dmodels");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");




        uploadFile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                openFile();            }
        });

        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });


        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });
    }

    private void openGallery() {
        //TO PICK AN IMAGE FROM GALLERY
        Intent GalleryIntent = new Intent();
        GalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        GalleryIntent.setType("image/*");
        startActivityForResult(GalleryIntent,GalleryPick);
    }
    private void openFile () {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICKFILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GalleryPick:
                if (resultCode == RESULT_OK) {
                    imageUri= data.getData();
                    InputProductImage.setImageURI(imageUri);
                    Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                }
                break;

            case PICKFILE_REQUEST_CODE:
                if(requestCode==PICKFILE_REQUEST_CODE) {
                    fileUri= data.getData();
                    //InputProductImage.setImageURI(fileUri);
                    Toast.makeText(this, "File Uploaded", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            mImageView.setImageURI(imageUri);

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputProductImage.setImageBitmap(bitmap);

            Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show();

        } else if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            mImageView.setImageURI(imageUri);
        }
    }*/

    private void ValidateProductData() {
        category = InputProductCateogyr.getText().toString();
        Price = InputProductPrice.getText().toString();
        Pname = InputProductName.getText().toString();


        if (imageUri == null) {
            Toast.makeText(this, "Product image is mandatory...", Toast.LENGTH_SHORT).show();
        }
        else if (fileUri == null) {
            Toast.makeText(this, "Product File is mandatory...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(category)) {
            Toast.makeText(this, "Please write product description...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Price)) {
            Toast.makeText(this, "Please write product Price...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Pname)) {
            Toast.makeText(this, "Please write product name...", Toast.LENGTH_SHORT).show();
        } else {
            StoreProductInformation();
        }
    }


    private void StoreProductInformation() {
        loadingBar.setTitle("Add New Product");
        loadingBar.setMessage("Please wait while adding the new product.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;


        final StorageReference fileImagePath = ProductImagesRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final StorageReference filePath = productFileRef.child(fileUri.getLastPathSegment() + productRandomKey + ".sfb");




        //Upload 3d Model
        final UploadTask uploadFileTask = filePath.putFile(fileUri);
        uploadFileTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AddProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }

        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                double progress = (100.0 * snapshot.getBytesTransferred()/ snapshot.getTotalByteCount());
                loadingBar.setMessage(new StringBuilder("Uploading: ") .append(progress).append("%"));
            }

        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddProductActivity.this, "Product File uploaded Successfully...", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadFileTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        downloadFileUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }

                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            //loadingBar.dismiss();
                            downloadFileUrl = task.getResult().toString();
                            Toast.makeText(AddProductActivity.this, "got the Product File Url Successfully...", Toast.LENGTH_SHORT).show();
                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });

        //Upload Image
        final UploadTask uploadTask = fileImagePath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AddProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }

        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddProductActivity.this, "Product Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        downloadImageUrl = fileImagePath.getDownloadUrl().toString();
                        return fileImagePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            //loadingBar.dismiss();
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AddProductActivity.this, "got the Product image Url Successfully...", Toast.LENGTH_SHORT).show();
                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });

    }


    private void SaveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
//        productMap.put("date", saveCurrentDate);
//        productMap.put("time", saveCurrentTime);
        productMap.put("description", Description);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", category);
        productMap.put("price", Price);
        productMap.put("name", Pname);
        productMap.put("model", downloadFileUrl);

        ProductsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //  loadingBar.dismiss();
//                            Intent intent = new Intent(AddProductActivity.this, HomeActivity.class);
//                            startActivity(intent);

                            Toast.makeText(AddProductActivity.this, "Product is added successfully..", Toast.LENGTH_SHORT).show();
                        } else {
                          //  loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AddProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        loadingBar.dismiss();
    }


}
