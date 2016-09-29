package com.nanodegree.myapps.popularmovies;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TempActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)  findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("This is a Heart");
    }
}
