package com.prp.studybuddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private  static int lengthofdrawermenu=0;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    TextView name,email;
    ImageView im;
    String firebase_userEmail,pref,profile,experties,u_name;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //side drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view) ;

        View mn= mNavigationView.inflateHeaderView(R.layout.nav_header_main);
        name= (TextView) mn.findViewById(R.id.name);
        email=  (TextView) mn.findViewById(R.id.email);
        im = (ImageView) mn.findViewById(R.id.profilee);

        getCurrentuser();



        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);
        setSupportActionBar(toolbar);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();
        //side drawer end

        //tabs
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        //tabs end

    }

    //side drawer
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null && item.getItemId() == android.R.id.home) {
            toggle();
        }
        return super.onOptionsItemSelected(item);
    }
    private void toggle() {
        if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }
    private void getCurrentuser()
    {
        FirebaseUser firebase_user = FirebaseAuth.getInstance().getCurrentUser();
        if (firebase_user != null) {
            name.setText(firebase_user.getDisplayName());
            email.setText(firebase_user.getEmail());
            firebase_userEmail=firebase_user.getEmail();
        }
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot topSnapshot) {
                for (DataSnapshot snapshot: topSnapshot.getChildren()) {
                    user USER = snapshot.getValue(user.class);
                    if (USER != null && USER.email.equals(firebase_userEmail)) {
                         pref=USER.pref;
                         profile=USER.profile;
                         experties= USER.experties;
                         u_name=USER.name;
                         name.setText(u_name);

                    }
                    else
                        System.out.println("**********No Match data found");
                }

                sideDrawerMenu();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("TAG", "Failed to read user", error.toException());
            }
        });
    }
    public void downloadProfile() throws IOException {
        StorageReference riversRef = mStorageRef.child(profile);
        if(im!=null){
            System.out.println("*************Inside");
            Glide.with(this).load(riversRef).into(im);
            System.out.println("*************Outside");
        }

    }
    public void sideDrawerMenu(){
        final Menu drawerMenu= mNavigationView.getMenu();
        ArrayList<String> items = new ArrayList<String>(Arrays.asList(pref.split(",")));
        int i;
        for(i=0;i<items.size();i++)
        {
            String s=items.get(i);
            drawerMenu.add(s);
            drawerMenu.getItem(i).setIcon(R.drawable.arrow);
        }
        drawerMenu.add("All");
        drawerMenu.getItem(i).setIcon(R.drawable.squad);
        drawerMenu.add("My Questions");
        drawerMenu.getItem(i+1).setIcon(R.drawable.ss);
        drawerMenu.add("Edit Preferences");
        drawerMenu.getItem(i+2).setIcon(R.drawable.composea);
        drawerMenu.add("Sign Out");
        drawerMenu.getItem(i+3).setIcon(R.drawable.sign_out);
        lengthofdrawermenu=i+4;

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                String draweritem = (String) menuItem.getTitle();
                if(draweritem.contains("Sign Out"))
                {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, Login.class));
                }
                mDrawerLayout.closeDrawers();
                return false;
            }

        });

    }
    //side drawer end

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("no", null).show();
    }

    //tabs
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new global(), "Global");
        adapter.addFragment(new wall(), "Wall");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    //tabs end
}
