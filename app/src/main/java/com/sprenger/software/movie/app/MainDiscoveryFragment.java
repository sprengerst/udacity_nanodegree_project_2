/*
 * Created by Stefan Sprenger
 */

package com.sprenger.software.movie.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.sprenger.software.movie.app.configuration.MovieGridAdapter;
import com.sprenger.software.movie.app.database.MovieContract;
import com.sprenger.software.movie.app.download.FetchMovieDataTask;
import com.sprenger.software.movie.app.utilities.Utility;

public class MainDiscoveryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private MovieGridAdapter movieGridAdapter;
    private static final int CURSOR_LOADER_ID = 0;

    private int mCurrentPos;
    private static final String SELECTED_KEY = "selected_position";
    private GridView mMovieGridview;

    public MainDiscoveryFragment() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refetch) {
            updateMovieGrid();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreate(Bundle instance) {
        super.onCreate(instance);
        setHasOptionsMenu(true);
    }

    private void updateMovieGrid() {
        if (Utility.isNetworkAvailable(getContext())) {
            try {
                FetchMovieDataTask fetchMovieTask = new FetchMovieDataTask(this);
                fetchMovieTask.execute();
            } catch (Exception e) {
                Log.e("Sync Error", e.toString());
                e.printStackTrace();
            }
        } else {
            Utility.showToast("Check your Network Connection", getContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.movieGridAdapter = new MovieGridAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.movie_discovery_fragment, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movie_images);
        gridView.setAdapter(this.movieGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(MovieContract.MovieEntry.buildMovieByMovieId(cursor.getString(Utility.COL_MOVIE_MOVIEDBID))
                            );
                }
                mCurrentPos = position;
            }
        });


        mMovieGridview = gridView;


        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mCurrentPos = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mCurrentPos != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mCurrentPos);
        }

        super.onSaveInstanceState(outState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrderSQL = Utility.getSortOrderSQL(
                Utility.getPreferedSortOrder(getContext()
                ));

        boolean onlyFavorite = Utility.getOnlyFavoriteOption(getContext());

        String selectionColumn = null;
        String[] selectionArgs = null;

        if (onlyFavorite) {
            selectionColumn = MovieContract.MovieEntry.COLUMN_IS_FAVORITE + "= ?";
            selectionArgs = new String[]{"1"};
        }

        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                Utility.MOVIE_COLUMNS,
                selectionColumn,
                selectionArgs,
                sortOrderSQL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieGridAdapter.swapCursor(data);
        if (mCurrentPos != GridView.INVALID_POSITION) {
            mMovieGridview.smoothScrollToPosition(mCurrentPos);
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieGridAdapter.swapCursor(null);
    }

    void onSortOrderChanged() {
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }

    public interface Callback {
        void onItemSelected(Uri dateUri);
    }

}