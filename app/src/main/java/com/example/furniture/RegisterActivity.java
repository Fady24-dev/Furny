package com.example.furniture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.furniture.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {
    private ImageView backBtn;
    private EditText userName,password,email;
    private Button signUpBtn;
    private ProgressDialog loadingbar ;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        backBtn=findViewById(R.id.back_btn);
        signUpBtn=findViewById(R.id.sign_up_btn);

        userName=findViewById(R.id.username_input_signup);
        password=findViewById(R.id.password_input_signup);
        email=findViewById(R.id.email_input_singup);

        loadingbar= new ProgressDialog(this);

        if(mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount() ;
            }
        });
    }

    private void CreateAccount() {
        String name = userName.getText().toString().trim();
        String mail = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if(TextUtils.isEmpty(name)) {
            userName.setError("Username is required");
            userName.requestFocus();
            return;
        }
         if(TextUtils.isEmpty(mail)) {
            email.setError("Email is required");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            email.setError("Please provide valid email");
            email.requestFocus();
            return;

        }
         if(TextUtils.isEmpty(pass)) {
            password.setError("Password is required");
            password.requestFocus();
            return;
        }
         if(pass.length() < 6 ) {
           password.setError("Password must be at least 6 characters");
           password.requestFocus();
            return;

        }


        else {
            loadingbar.setTitle("Create Account");
            loadingbar.setMessage("Creating new account.");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            ValidateEmail(name,mail,pass);
        }
    }



    private void ValidateEmail(String name, String mail, String pass) {



        mAuth.createUserWithEmailAndPassword(mail,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Users user = new Users(name,mail);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                    }
                                    else {
                                        Toast.makeText(RegisterActivity.this, ""+task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                        Log.d("moshkla",""+task.getException());
                                    }
                                }
                            });

                        }
                        else {
                            Toast.makeText(RegisterActivity.this, ""+task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();


                        }


                    }
                });

    }
}
