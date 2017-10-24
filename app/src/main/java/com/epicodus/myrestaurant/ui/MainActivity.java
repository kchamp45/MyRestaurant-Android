package com.epicodus.myrestaurant.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.epicodus.myrestaurant.Constants;
import com.epicodus.myrestaurant.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private DatabaseReference mSearchedLocationReference;
    private ValueEventListener mSearchedLocationReferenceListener;
    @Bind(R.id.savedRestaurantsButton) Button mSavedRestaurantsButton;
    @Bind(R.id.findRestaurantButton) Button mFindRestaurantButton;
    @Bind(R.id.locationEditText) EditText mLocationEditText;
    @Bind(R.id.appNameTextView) TextView mAppNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSearchedLocationReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(Constants.FIREBASE_CHILD_SEARCHED_LOCATION);

        mSearchedLocationReference.addValueEventListener(new ValueEventListener() { //attach listener

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { //something changed!
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    String location = locationSnapshot.getValue().toString();
                    Log.d("Locations updated", "location: " + location); //log
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { //update UI here if error occurred.

            }
        });


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();

        Typeface ostrichFont = Typeface.createFromAsset(getAssets(), "fonts/ostrich-regular.ttf");
        mAppNameTextView.setTypeface(ostrichFont);

        mFindRestaurantButton.setOnClickListener(this);
        mSavedRestaurantsButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == mFindRestaurantButton) {
            String userLocation = mLocationEditText.getText().toString();
            addToSharedPreferences(userLocation);
            saveLocationToFirebase(userLocation);

            Intent intent = new Intent(MainActivity.this, RestaurantListActivity.class);
            intent.putExtra("location", userLocation);
            startActivity(intent);
        }
        if (v == mSavedRestaurantsButton) {
            Intent intent = new Intent(MainActivity.this, SavedRestaurantListActivity.class);
            startActivity(intent);
        }
    }

    private void addToSharedPreferences(String location) {
        mEditor.putString(Constants.PREFERENCES_LOCATION_KEY, location).apply();
    }
    public void saveLocationToFirebase(String location) {
        mSearchedLocationReference.push().setValue(location);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSearchedLocationReference.removeEventListener(mSearchedLocationReferenceListener);
    }
}
