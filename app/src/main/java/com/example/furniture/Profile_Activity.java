package com.example.furniture;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.furniture.models.Users;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class Profile_Activity extends AppCompatActivity {
    private TextView profileUserName;
    private EditText profileUserEmailEdit,profileUserPhoneEdit,profileUserAddressEdit,profileUserFacebookEdit;
    private ImageView profileImage;
    private Button editButton,submitButton;
    private DatabaseReference reference;
    private FirebaseUser user;
    private String userID;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();
        userID=user.getUid();
        profileUserName=findViewById(R.id.profile_user_name);
        profileImage=findViewById(R.id.user_image);
        editButton=findViewById(R.id.profile_edit_button);
        submitButton=findViewById(R.id.profile_submit_button);
        profileUserEmailEdit=findViewById(R.id.profile_user_email_edit);
        profileUserPhoneEdit=findViewById(R.id.profile_user_phone_edit);
        profileUserAddressEdit=findViewById(R.id.profile_user_address_edit);
        profileUserFacebookEdit=findViewById(R.id.profile_user_facebook_edit);

        getCurrentUserData();

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editButton.setVisibility(View.INVISIBLE);
                submitButton.setVisibility(View.VISIBLE);

                profileUserEmailEdit.setEnabled(true);
                profileUserPhoneEdit.setEnabled(true);
                profileUserAddressEdit.setEnabled(true);
                profileUserFacebookEdit.setEnabled(true);

                readData();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileUserEmailEdit.setEnabled(false);
                profileUserPhoneEdit.setEnabled(false);
                profileUserAddressEdit.setEnabled(false);
                profileUserFacebookEdit.setEnabled(false);

                editButton.setVisibility(View.VISIBLE);
                submitButton.setVisibility(View.INVISIBLE);

                submitData();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
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
                Uri imageUri = data.getData();

                uploadImage(imageUri);
            }
        }
    }

    private void uploadImage(Uri imageUri) {
        StorageReference fileRef = storageReference.child("UserImages/"+userID+"/profile.jpg");
        fileRef.putFile(imageUri);

        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                reference.child(userID).child("image").setValue(uri.toString());
            }
        });
    }

    private void readData() {
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    Users user = new Users();
                    user = snapshot.getValue(Users.class);

                    profileUserEmailEdit.setText(user.getEmail());
                    profileUserPhoneEdit.setText(user.getPhone());
                    profileUserAddressEdit.setText(user.getAddress());
                    profileUserFacebookEdit.setText(user.getFacebook());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void submitData() {

        final HashMap<String, Object> profileMap = new HashMap<>();
        profileMap.put("phone",profileUserPhoneEdit.getText().toString());
        profileMap.put("address",profileUserAddressEdit.getText().toString());
        profileMap.put("facebook",profileUserFacebookEdit.getText().toString());

        reference.child(userID).updateChildren(profileMap);

        getCurrentUserData();
    }

    private void getCurrentUserData() {
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);

                if(user != null){
                    String username= user.getName();
                    String email= user.getEmail();
                    String image = user.getImage();
                    String phone = user.getPhone();
                    String address = user.getAddress();
                    String facebook = user.getFacebook();

                    profileUserName.setText(username);
                    profileUserEmailEdit.setText(email);
                    profileUserPhoneEdit.setText(phone);
                    profileUserAddressEdit.setText(address);
                    profileUserFacebookEdit.setText(facebook);

                    Picasso.with(getApplicationContext()).load(image).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
