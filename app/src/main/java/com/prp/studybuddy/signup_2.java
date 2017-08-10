package com.prp.studybuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class signup_2 extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    CheckBox cbp,java,c,android,python;
    EditText editTextname;
    TextView tvmain,tvother;
    Button tvnext;
    String rcvname,rcvemail,rcvpass,caller,email;
    LinearLayout linear;
    boolean flag=true;
    FirebaseDatabase database;
    DatabaseReference ref;
    ArrayList<String> arrayList = new ArrayList<String>();
    void init()
    {
        cbp=(CheckBox)findViewById(R.id.cbcbp);
        c=(CheckBox)findViewById(R.id.cbc);
        java=(CheckBox)findViewById(R.id.cbjava);
        android=(CheckBox)findViewById(R.id.cbandroid);
        python=(CheckBox)findViewById(R.id.cbpython);
        tvmain=(TextView)findViewById(R.id.tvpreferences);
        tvnext=(Button) findViewById(R.id.tvnext);
        linear=(LinearLayout)findViewById(R.id.Linear);
        editTextname = (EditText)findViewById(R.id.editTextname);
        Intent rcv =getIntent();
        rcvname = rcv.getStringExtra("Keyname");
        rcvemail = rcv.getStringExtra("Keyemail");
        rcvpass = rcv.getStringExtra("Keypass");
        if(rcvname==null)
            linear.setVisibility(View.VISIBLE);
        cbp.setOnCheckedChangeListener(this);
        c.setOnCheckedChangeListener(this);
        java.setOnCheckedChangeListener(this);
        android.setOnCheckedChangeListener(this);
        python.setOnCheckedChangeListener(this);
        tvnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rcvname==null || rcvname.length()<5 )
                    rcvname=editTextname.getText().toString().trim();
                Intent i2 = new Intent(signup_2.this, signup_3.class);
                    i2.putExtra("KeyArray", arrayList);
                    i2.putExtra("Keyname", rcvname);
                    i2.putExtra("Keyemail", rcvemail);
                    i2.putExtra("Keypass", rcvpass);
                if(rcvname.length()<5)
                    Toast.makeText(signup_2.this, "Name should be minimum of length 5", Toast.LENGTH_SHORT).show();
                else if(arrayList.isEmpty())
                    Toast.makeText(signup_2.this, "Not Selected Any Field", Toast.LENGTH_SHORT).show();
                else
                    startActivity(i2);

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_2);
        Intent intent = getIntent();
        caller = intent.getStringExtra("activity");
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("users");
        check();
        init();

    }
    private void check() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email = user.getEmail();
        }
        System.out.println("email"+email);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot topSnapshot) {
                for (DataSnapshot snapshot : topSnapshot.getChildren()) {
                    user u = snapshot.getValue(user.class);
                    System.out.println("u.email"+u.email);
                    if(u.email==email) {
                        flag = false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "Failed to read user");
            }
        });
        System.out.println("flag:"+flag);
        if(!flag)
        {
            Intent i3 = new Intent(signup_2.this, MainActivity.class);
            startActivity(i3);
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId()==R.id.cbc)
        {
            if(isChecked)
                arrayList.add(c.getText().toString().trim());
            else
                arrayList.remove(c.getText().toString().trim());
        }
        if(buttonView.getId()==R.id.cbcbp)
        {
            if(isChecked)
                arrayList.add(cbp.getText().toString().trim());
            else
                arrayList.remove(cbp.getText().toString().trim());
        }
        if(buttonView.getId()==R.id.cbjava)
        {
            if(isChecked)
                arrayList.add(java.getText().toString().trim());
            else
                arrayList.remove(java.getText().toString().trim());
        }
        if(buttonView.getId()==R.id.cbpython)
        {
            if(isChecked)
                arrayList.add(python.getText().toString().trim());
            else
                arrayList.remove(python.getText().toString().trim());
        }
        if(buttonView.getId()==R.id.cbandroid)
        {
            if(isChecked)
                arrayList.add(android.getText().toString().trim());
            else
                arrayList.remove(android.getText().toString().trim());
        }

    }
}
