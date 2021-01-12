package com.example.furniture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.ar.sceneform.ux.ArFragment;

public class PurchaseActivity extends AppCompatActivity {
    Button previewBtn ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_purchase);
            previewBtn = findViewById(R.id.selected_item_preview_btn);
            previewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PurchaseActivity.this, ArActivity.class);
                    startActivity(intent);
                }
            });
    }
}
