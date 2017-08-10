package com.prp.studybuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class signup_1 extends AppCompatActivity implements View.OnClickListener {
    Button signin;
    EditText name, email, pass, cpass;
    private FirebaseAuth mAuth;
    boolean b=false;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    void init() {
        signin = (Button) findViewById(R.id.btnSignup);
        name = (EditText) findViewById(R.id.editTextname);
        email = (EditText) findViewById(R.id.editTextemail);
        pass = (EditText) findViewById(R.id.editTextpass);
        cpass = (EditText) findViewById(R.id.editTextcpass);
        signin.setOnClickListener(this);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_1);
        init();
    }
    public void checkAccountEmailExistInFirebase() {
        mAuth = FirebaseAuth.getInstance();
        String eemail = email.getText().toString().trim();
        if(eemail.length()!=0) {
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot topSnapshot) {
                    for (DataSnapshot snapshot: topSnapshot.getChildren()) {
                        user USER = snapshot.getValue(user.class);
                        System.out.println("*********User Name:" + USER.name);
                        if (USER != null && USER.email.equals(email.getText().toString().trim())) {
                            b=true;
                        }
                        else
                            System.out.println("**********Null data found");
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.e("TAG", "Failed to read user", error.toException());
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        String spass, scpass, sname, semail;

        spass = pass.getText().toString().trim();
        scpass = cpass.getText().toString().trim();
        sname = name.getText().toString().trim();
        semail = email.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        checkAccountEmailExistInFirebase();

        if (sname != null && semail != null && scpass != null && spass != null) {
            if(sname.length()<5) {
                name.setText(null);
                Toast.makeText(signup_1.this, "Name should be minimum of length 5", Toast.LENGTH_SHORT).show();
            }
            else if(spass.length() < 8 || !spass.matches(".*\\d+.*") || !spass.matches(".*[a-z].*")) {
                pass.setText(null);
                cpass.setText(null);
                if (spass.length() < 8)
                    Toast.makeText(signup_1.this, "Password should be minimum of length 8", Toast.LENGTH_SHORT).show();
                else if(!spass.matches(".*[a-z].*"))
                    Toast.makeText(signup_1.this, "Password should contain Characters", Toast.LENGTH_SHORT).show();
                else if (!spass.matches(".*\\d+.*"))
                    Toast.makeText(signup_1.this, "Password should contain Numbers", Toast.LENGTH_SHORT).show();
            }
            else if (spass.compareTo(scpass) == 0 && semail.matches(emailPattern) && !b) {
                Intent i1 = new Intent(signup_1.this, signup_2.class);
                i1.putExtra("Keyname", sname);
                i1.putExtra("Keyemail", semail);
                i1.putExtra("Keypass", spass);

                SharedPreferences sp1 = getApplicationContext().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp1.edit();
                editor.putString("name", sname);
                editor.apply();
                editor.commit();

                SharedPreferences sp2 = getApplicationContext().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorr = sp2.edit();
                editorr.putString("email", semail);
                editorr.apply();
                editorr.commit();
                startActivity(i1);
            } else if (!semail.matches(emailPattern)) {
                Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                email.setText(null);
            }
            else if (b) {
                Toast.makeText(getApplicationContext(), "Email alredy Registered", Toast.LENGTH_SHORT).show();
                email.setText(null);
            }else {
                pass.setText(null);
                cpass.setText(null);
                Toast.makeText(this, "Confirm Password Not Matched", Toast.LENGTH_LONG).show();
            }

        }
        else
        {
            if (sname == null) {
                name.setHighlightColor(Color.RED);
            }
            if (semail == null) {
                email.setHighlightColor(Color.RED);
            }
            if (spass == null) {
                pass.setHighlightColor(Color.RED);
            }
            if (scpass == null) {
                cpass.setHighlightColor(Color.RED);
            }
            Toast.makeText(this, "Incomplete Information!", Toast.LENGTH_SHORT).show();
        }
    }
}
