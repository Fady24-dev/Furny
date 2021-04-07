package com.example.furniture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.furniture.models.Users;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class Profile_Activity extends AppCompatActivity {
    private TextView profileUserName,profileEmailAddress,profileUserPhone,profileUserAddress,profileUserFacebook;
    private EditText profileUserEmailEdit,profileUserPhoneEdit,profileUserAddressEdit,profileUserFacebookEdit;
    private ImageView profileImage;
    private Button editButton,submitButton;
    private DatabaseReference reference;
    private FirebaseUser user;
    private String userID,currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users");
        userID=user.getUid();
        currentEmail=user.getEmail();
        profileUserName=findViewById(R.id.profile_user_name);
        profileEmailAddress=findViewById(R.id.profile_user_email);
        profileUserAddress=findViewById(R.id.profile_user_address);
        profileUserFacebook=findViewById(R.id.profile_user_facebook);
        profileImage=findViewById(R.id.user_image);
        profileUserPhone=findViewById(R.id.profile_user_phone);
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
                profileEmailAddress.setVisibility(View.INVISIBLE);
                profileUserEmailEdit.setVisibility(View.VISIBLE);

                profileUserPhone.setVisibility(View.INVISIBLE);
                profileUserPhoneEdit.setVisibility(View.VISIBLE);

                profileUserAddress.setVisibility(View.INVISIBLE);
                profileUserAddressEdit.setVisibility(View.VISIBLE);

                profileUserFacebook.setVisibility(View.INVISIBLE);
                profileUserFacebookEdit.setVisibility(View.VISIBLE);

                editButton.setVisibility(View.INVISIBLE);
                submitButton.setVisibility(View.VISIBLE);

                readData();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileEmailAddress.setVisibility(View.VISIBLE);
                profileUserEmailEdit.setVisibility(View.INVISIBLE);

                profileUserPhone.setVisibility(View.VISIBLE);
                profileUserPhoneEdit.setVisibility(View.INVISIBLE);

                profileUserAddress.setVisibility(View.VISIBLE);
                profileUserAddressEdit.setVisibility(View.INVISIBLE);

                profileUserFacebook.setVisibility(View.VISIBLE);
                profileUserFacebookEdit.setVisibility(View.INVISIBLE);

                editButton.setVisibility(View.VISIBLE);
                submitButton.setVisibility(View.INVISIBLE);

                submitData();
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
                    profileEmailAddress.setText(email);
                    profileUserPhone.setText(phone);
                    profileUserAddress.setText(address);
                    profileUserFacebook.setText(facebook);

                    Picasso.with(getApplicationContext()).load(user.getImage()).into(profileImage);
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
