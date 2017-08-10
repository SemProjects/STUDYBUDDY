package com.prp.studybuddy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class signup_3 extends AppCompatActivity implements  CompoundButton.OnCheckedChangeListener {
    private static final int RESULT_LOAD_IMAGE = 1;
    Uri selectedImage;
    RadioButton yes, no;
    LinearLayout linear;
    ArrayList<String> array;
    ArrayList<String> arr_exp;
    Button tvdone;
    ArrayAdapter<String> adapter;
    String pref,experties;
    ImageView im, ivAttachment;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private StorageReference mStorageRef;
    String rcvname;
    String rcvemail;
    String rcvpass;
    String uid;
    FirebaseDatabase database;
    DatabaseReference ref;
    void init() {
        tvdone = (Button) findViewById(R.id.tvdone);
        yes = (RadioButton) findViewById(R.id.rbyes);
        no = (RadioButton) findViewById(R.id.rbno);
        yes.setOnCheckedChangeListener(this);
        no.setOnCheckedChangeListener(this);
        linear=(LinearLayout)findViewById(R.id.Linear);
        linear.setVisibility(View.INVISIBLE);
        Intent rcv1 = getIntent();
        array = rcv1.getStringArrayListExtra("KeyArray");
        arr_exp=new ArrayList<String>();
        rcvname = rcv1.getStringExtra("Keyname");
        rcvemail = rcv1.getStringExtra("Keyemail");
        rcvpass = rcv1.getStringExtra("Keypass");
        im = (ImageView) findViewById(R.id.profilee);
        pref = "";
        experties = "";

        tvdone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String s : arr_exp) {
                    System.out.println(":"+s+":");
                    experties = experties + s + ",";
                }
                for (String s : array) {
                    System.out.println(":"+s+":");
                    pref = pref + s + ",";
                }
                putFile();
            }
        });
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gintent, RESULT_LOAD_IMAGE);
            }
        });
    }
    protected void putFile()
    {
        if(selectedImage != null) {
            final ProgressDialog progressDialog = new ProgressDialog(signup_3.this);
            progressDialog.setMessage("Setting profile");
            progressDialog.show();
            StorageReference riversRef = mStorageRef.child("profileImages/" +rcvemail+".jpg");

            riversRef.putFile(selectedImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            // Get a URL to the uploaded content
                            // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            if(rcvemail==null) {
                                getCurrentuser();
                                ref.child(uid).setValue(new user(rcvname,"profileImages/" +rcvemail+".jpg",pref,experties,rcvemail));

                                Intent i = new Intent(signup_3.this, MainActivity.class);
                                startActivity(i);
                            }
                            else
                                createAccount(rcvemail, rcvpass);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });
                   /* .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressdialog.setMessage(((int) progress) + " % Uploaded..");
                }
            });*/
        }
        else {
            Toast.makeText(signup_3.this, "Not Selcted Any Image", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();
            im.setImageURI(selectedImage);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_3);
        init();
        firebasesignin();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("users");
    }
    void firebasesignin()
    {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                     uid = user.getUid();
                    // User is signed in
                    Log.d("tag", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("tag", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }
    private void createAccount(String email, String password) {
        final ProgressDialog progressDialog = new ProgressDialog(signup_3.this);
        progressDialog.setMessage("Signing In....");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG", "createUserWithEmail:onComplete:" + task.isSuccessful());
                        progressDialog.dismiss();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(signup_3.this, "Failed Try again", Toast.LENGTH_LONG).show();
                        }
                        else {
                            getCurrentuser();
                            ref.child(uid).setValue(new user(rcvname,"profileImages/" +rcvemail+".jpg",pref,experties,rcvemail));

                            Intent i = new Intent(signup_3.this, MainActivity.class);
                            startActivity(i);
                        }

                        // ...
                    }
                });

    }
    private void getCurrentuser()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
            if(rcvemail==null )
            {
                rcvemail = user.getEmail();
            }
        }
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int x = buttonView.getId();
        if (x == R.id.rbyes && buttonView.isChecked()) {
            linear.setVisibility(View.VISIBLE);
            for (String s : array) {
                CheckBox cb = new CheckBox(getApplicationContext());
                cb.setText(s);
                linear.addView(cb);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (buttonView.isChecked()) {
                            arr_exp.add(buttonView.getText().toString().trim());
                        } else {
                            arr_exp.remove(buttonView.getText().toString().trim());
                        }
                    }

                    });
               }
        }
        else if (x == R.id.rbno && buttonView.isChecked()) {
            linear.setVisibility(View.INVISIBLE);
            experties = "";
            linear.removeAllViews();
            arr_exp.clear();
        }

    }
}
