package com.nanodegree.myapps.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ReviewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        ListView reviewsList = (ListView) findViewById(R.id.reviews_listview);
        TextView emptyView = (TextView) findViewById(R.id.no_reviews_tv);

        Intent intent = getIntent();

        ArrayAdapter<String> reviewsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, intent.getStringArrayListExtra(DetailsActivity.REVIEWS_KEY));
        reviewsList.setAdapter(reviewsAdapter);

        reviewsList.setEmptyView(emptyView);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            NavUtils.navigateUpTo(this, upIntent);
        }

        return true;
    }
}
