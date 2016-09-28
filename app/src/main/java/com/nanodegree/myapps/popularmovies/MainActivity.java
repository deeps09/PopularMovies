package com.nanodegree.myapps.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback {
    static String LOG_TAG = MainActivity.class.getSimpleName();
    boolean mTwoPaneUI = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.detail_fragment_container) != null){

            mTwoPaneUI = true;

            /*if (savedInstanceState == null){
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.detail_fragment_container, new DetailsFragment())
                        .commit();
            }*/
        }
    }

    @Override
    public void onItemSelected(Movies movie) {
        if (mTwoPaneUI){

            Bundle bundle = new Bundle();
            bundle.putParcelable("bundle", movie);

            DetailsFragment fragment = new DetailsFragment();
            fragment.setArguments(bundle);

            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail_fragment_container, fragment, "tag1")
                    .commit();
        } else{
            Intent intent = new Intent(this, DetailsActivity.class)
                    .putExtra("bundle", movie);
            startActivity(intent);

        }
    }
}
