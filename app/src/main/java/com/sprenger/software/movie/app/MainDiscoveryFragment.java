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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.sprenger.software.movie.app.data.MovieContract;

public class MainDiscoveryFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor> {

    private MovieGridAdapter movieGridAdapter;
    private static final int CURSOR_LOADER_ID = 0;

    private int mCurrentPos;

    public MainDiscoveryFragment() {
    }

    @Override
    public void onCreate(Bundle instance) {
        super.onCreate(instance);
        setHasOptionsMenu(true);
    }

    private void updateMovieGrid() {
        try {
            FetchMovieDataTask fetchMovieTask = new FetchMovieDataTask(this);
            fetchMovieTask.execute();
        } catch (Exception e) {
            Log.e("Sync Error",e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // FIXME
        updateMovieGrid();

        this.movieGridAdapter = new MovieGridAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_discovery_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movie_images);
        gridView.setAdapter(this.movieGridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String sortOrder = Utility.getPreferedSortOrder(getActivity());
                    ((Callback) getActivity())
                            .onItemSelected(MovieContract.MovieEntry.buildMovieByPosterID(cursor.getString(Utility.COL_MOVIE_POSTER_PATH))
                            );
                }
                mCurrentPos = position;
            }
        });


        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){

        String sortOrder = Utility.getPreferedSortOrder(getContext());
        String sortOrderSQL;

        if(sortOrder.equals("most_popular")){
            sortOrderSQL = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        }else{
            sortOrderSQL = MovieContract.MovieEntry.COLUMN_RATING+ " DESC";
        }


        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                Utility.MOVIE_COLUMNS,
                null,
                null,
                sortOrderSQL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        movieGridAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        movieGridAdapter.swapCursor(null);
    }

    // since we read the location when we create the loader, all we need to do is restart things
    void onSortOrderChanged( ) {

        System.out.println("SORTORDERCHANGED");
        updateMovieGrid();
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }


    public interface Callback {
        public void onItemSelected(Uri dateUri);
    }

}