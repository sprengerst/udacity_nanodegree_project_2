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

import com.sprenger.software.movie.app.data.MovieProvider;

public class MainDiscoveryFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor> {

    private MovieGridAdapter movieGridAdapter;
    private static final int CURSOR_LOADER_ID = 0;

    public MainDiscoveryFragment() {
    }

    @Override
    public void onCreate(Bundle instance) {
        super.onCreate(instance);
        setHasOptionsMenu(true);
    }

    private void updateMovieGrid() {
        try {
//            AsyncTask<String, Void, ArrayList<MovieSpecification>> fetchMovieTask = new FetchMovieDataTask(this);
//
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortOrder = prefs.getString(getString(R.string.pref_sortorder_key), getResources().getStringArray(R.array.pref_sortorder_keystore)[0]);

//            assert movieGridAdapter != null;
//            movieGridAdapter.clear();
//
//            movieGridAdapter.addAll(fetchMovieTask.execute(sortOrder).get());


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

        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);

        // TODO check if this makes sense

        getContext().getContentResolver().delete(MovieProvider.Movies.CONTENT_URI, null, null);

        Cursor c = getActivity().getContentResolver().query(MovieProvider.Movies.CONTENT_URI,
                null, null, null, null);


        if (c == null || c.getCount() == 0){
            updateMovieGrid();
        }

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        return new CursorLoader(getActivity(), MovieProvider.Movies.CONTENT_URI,
                null,
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