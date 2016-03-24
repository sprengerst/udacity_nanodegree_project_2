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
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DETAIL_LOADER = 0;
    static final String DETAIL_URI = "DETAIL_URI";
    private Uri mUri;
    private ImageView mMoviePosterView;
    private TextView mMovieSynopsisView;
    private TextView mMovieTitleView;
    private ListView mMovieDetailsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
        }


        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mMoviePosterView = (ImageView) rootView.findViewById(R.id.image_view_detail);
        mMovieSynopsisView = (TextView) rootView.findViewById(R.id.synopsis_text_detail);
        mMovieTitleView = (TextView) rootView.findViewById(R.id.title_text_detail);
        mMovieDetailsList = (ListView) rootView.findViewById(R.id.movie_details_list);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


//TODO
//    void onLocationChanged( String newLocation ) {
//        // replace the uri, since the location has changed
//        Uri uri = mUri;
//        if (null != uri) {
//            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
//            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
//            mUri = updatedUri;
//            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
//        }
//    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    Utility.MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

        Picasso
                .with(getActivity())
                .load(data.getString(Utility.COL_MOVIE_POSTER_PATH))
                .into(mMoviePosterView);

            mMovieTitleView.setText(data.getString(Utility.COL_MOVIE_TITLE));

            mMovieSynopsisView.setText(data.getString(Utility.COL_MOVIE_SYNOPSIS));
            mMovieSynopsisView.setMovementMethod(new ScrollingMovementMethod());

            List<String> movieDetailList = new ArrayList<>();
            movieDetailList.add(getString(R.string.release_text) + " " + data.getString(Utility.COL_MOVIE_RELEASE_DATE));
            movieDetailList.add(getString(R.string.rating_text) + " " + data.getString(Utility.COL_MOVIE_RATING) + "/10");

            ArrayAdapter<String> movieDetailAdapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, movieDetailList);

            mMovieDetailsList.setAdapter(movieDetailAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
