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
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity {
    private Button loginBtn ;
    private EditText loginEmail,loginPassword;
    private TextView signUp,forgotPassword;
    private ProgressDialog loadingbar;
    private String parentDbName ="Users" ;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth= FirebaseAuth.getInstance();

        signUp=findViewById(R.id.create_account);
        forgotPassword=findViewById(R.id.forgot_password);
        loginBtn=findViewById(R.id.login_btn);
        loginEmail = findViewById(R.id.email_input_login);
        loginPassword= findViewById(R.id.password_input_login);


        loadingbar= new ProgressDialog(this);






        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ForgotPassword.class));
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();

            }
        });


    }

    private void LoginUser() {
        String email = loginEmail.getText().toString().trim();
        String pass = loginPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            loginEmail.setError("Email is required");
            loginEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            loginEmail.setError("Please provide valid email");
            loginEmail.requestFocus();
            return;
        }

         if(TextUtils.isEmpty(pass)) {
            loginPassword.setError("Password is required");
            loginPassword.requestFocus();
            return;
        }
         if(pass.length() < 6 ) {
            loginPassword.setError("Password must be at least 6 characters");
            loginPassword.requestFocus();
            return;

        }
        else {
            loadingbar.setTitle("Logging in");
            loadingbar.setMessage("Please Wait.");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

           AllowAccessToAccount(email,pass);
        }
    }

    private void AllowAccessToAccount(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user.isEmailVerified()){
                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        finish();
                        loadingbar.dismiss();

                        // Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Check your email to verify your account!", Toast.LENGTH_LONG).show();
                        loadingbar.dismiss();

                    }


                }
                else
                {
                    Toast.makeText(MainActivity.this, "Failed to login! Please check your credentials" ,Toast.LENGTH_LONG).show();
                    loadingbar.dismiss();
                }

            }
        });

    }


}
