package com.example.furniture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.furniture.models.Products;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ArActivity extends AppCompatActivity {
    private ArFragment arFragment;
    DatabaseReference Ref;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    private FirebaseUser user;
    private String userID,prodID;
    Boolean isPurchase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        arFragment= (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        progressBar=findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);




        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        //Firebase RecyclerView
        Ref= FirebaseDatabase.getInstance().getReference().child("Cart List").child("User Cart").child(userID).child("Products");

        recyclerView = findViewById(R.id.ar_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this,6));



//
//        String prodId=getIntent().getStringExtra("id");
//        Ref= FirebaseDatabase.getInstance().getReference().child("Products");
//        Ref.child(prodId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    String productModel = snapshot.child("model").getValue().toString();
//                    arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
//                        Anchor anchor = hitResult.createAnchor();
//                        progressBar.setVisibility(View.VISIBLE);
//
//                        ModelRenderable.builder()
//                                .setSource(getApplicationContext(), Uri.parse(productModel))
//                                .build()
//                                .thenAccept(modelRenderable -> addModelToScene(anchor,modelRenderable))
//                                .exceptionally(throwable -> {
//                                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//                                    builder.setMessage(throwable.getMessage())
//                                            .show();
//                                    return null;
//                                });
//                    });
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
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
    @Override
    protected void onStart() {
        super.onStart();


        //To Preview Single Item From Purchase activity
        String activity = getIntent().getStringExtra("activity");
        Toast.makeText(this, activity, Toast.LENGTH_SHORT).show();
        if(activity=="purchase"){

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
        }

        else {
            //To Preview Multiple Item From Purchase activity

            FirebaseRecyclerOptions<Products> options =
                    new FirebaseRecyclerOptions.Builder<Products>()
                            .setQuery(Ref,Products.class)
                            .build();


            FirebaseRecyclerAdapter<Products,ArViewHolder> adapter= new FirebaseRecyclerAdapter<Products, ArViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull ArViewHolder holder, int position, @NonNull Products model) {
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    userID = user.getUid();

                    Picasso.with(getApplicationContext()).load(model.getImage()).into(holder.prodImage);

                    holder.prodImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            prodID =getRef(holder.getAdapterPosition()).getKey();
                            Ref= FirebaseDatabase.getInstance().getReference().child("Cart List").child("User Cart").child(userID).child("Products");

                            Ref.child(prodID).addValueEventListener(new ValueEventListener() {
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


                        }
                    });

                }

                @NonNull
                @Override
                public ArViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ar_item,parent,false);
                    ArViewHolder holder = new ArViewHolder(view);
                    return holder;
                }
            };
            recyclerView.setAdapter(adapter);
            adapter.startListening();
        }

    }


}
