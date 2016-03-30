/*
 * Created by Stefan Sprenger
 */

package com.sprenger.software.movie.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.sprenger.software.movie.app.utilities.Utility;


public class MainDiscoveryActivity extends AppCompatActivity implements MainDiscoveryFragment.Callback {

    private String mSortOrder;
    private boolean mOnlyFavorites;
    private boolean mTwoPane;

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSortOrder = Utility.getPreferedSortOrder(this);
        mOnlyFavorites = Utility.getOnlyFavoriteOption(this);

        setContentView(R.layout.movie_discovery_activity);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_discovery_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortOrder = Utility.getPreferedSortOrder(this);
        boolean onlyFavorites = Utility.getOnlyFavoriteOption(this);

        if (onlyFavorites != mOnlyFavorites || sortOrder != null && !sortOrder.equals(mSortOrder)) {

            MovieDetailFragment df = (MovieDetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (null != df) {
                df.updateLoadedEntry();
            }

            mOnlyFavorites = onlyFavorites;

            MainDiscoveryFragment ff = (MainDiscoveryFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_grid);
            if (null != ff) {
                ff.onSortOrderChanged();
            }

            mSortOrder = sortOrder;

        }
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {

            Bundle args = new Bundle();
            args.putParcelable(MovieDetailFragment.DETAIL_URI, contentUri);

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }
}
