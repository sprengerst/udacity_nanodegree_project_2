/*
 * Created by Stefan Sprenger
 */

package com.sprenger.software.movie.app;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.sprenger.software.movie.app.data.MovieContract;

public class MainDiscoveryFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor> {

    private MovieGridAdapter movieGridAdapter;
    private static final int CURSOR_LOADER_ID = 0;


    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_RATING,
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_TITLE = 1;
    static final int COL_MOVIE_SYNOPSIS = 2;
    static final int COL_MOVIE_POSTER_PATH = 3;
    static final int COL_MOVIE_RELEASE_DATE = 4;
    static final int COL_MOVIE_POPUlARITY = 5;
    static final int COL_MOVIE_RATING = 6;




    public MainDiscoveryFragment() {
    }

    @Override
    public void onCreate(Bundle instance) {
        super.onCreate(instance);
        setHasOptionsMenu(true);
    }

    private void updateMovieGrid() {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortOrder = prefs.getString(getString(R.string.pref_sortorder_key), getResources().getStringArray(R.array.pref_sortorder_keystore)[0]);

            FetchMovieDataTask fetchMovieTask = new FetchMovieDataTask(this);
            fetchMovieTask.execute(sortOrder);
        } catch (Exception e) {
            Log.e("Sync Error",e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        updateMovieGrid();

        this.movieGridAdapter = new MovieGridAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_discovery_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movie_images);
        gridView.setAdapter(this.movieGridAdapter);

        // TODO start Detailsview
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                                                Intent detailedViewIntent = new Intent(getActivity(), MovieDetailActivity.class).putExtra(Intent.EXTRA_SUBJECT, movieGridAdapter.getItem(i));
//                                                startActivity(detailedViewIntent);
                                            }
                                        }

        );


        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        return new CursorLoader(getActivity(), MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        movieGridAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        movieGridAdapter.swapCursor(null);
    }



}