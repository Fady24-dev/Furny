package com.example.furniture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ArActivity extends AppCompatActivity {
    private ArFragment arFragment;
    DatabaseReference Ref;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        arFragment= (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        progressBar=findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        String prodId=getIntent().getStringExtra("id");
        Ref= FirebaseDatabase.getInstance().getReference().child("Products");
        Ref.child(prodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String productModel = snapshot.child("model").getValue().toString();


                    arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
                        Anchor anchor = hitResult.createAnchor();
                        progressBar.setVisibility(View.VISIBLE);

                        ModelRenderable.builder()
                                .setSource(getApplicationContext(), Uri.parse(productModel))
                                .build()
                                .thenAccept(modelRenderable -> addModelToScene(anchor,modelRenderable))
                                .exceptionally(throwable -> {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                                    builder.setMessage(throwable.getMessage())
                                            .show();
                                    return null;
                                });
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //String prodUri ="https://firebasestorage.googleapis.com/v0/b/furnitureapp-a03d7.appspot.com/o/3Dmodels%2Fchair1.sfb?alt=media&token=f2730282-4d94-4b3d-9df8-7c56f09de14b";



    }

    private void addModelToScene(Anchor anchor, ModelRenderable modelRenderable) {
        progressBar.setVisibility(View.GONE);

        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());

        transformableNode.getScaleController().setEnabled(false);

        transformableNode.setParent(anchorNode);
        transformableNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        transformableNode.select();

    }
}
