package com.nanodegree.myapps.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (savedInstanceState == null){
            getFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, new DetailsFragment()).commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        NavUtils.navigateUpTo(this, upIntent);

        return true;
    }
}

