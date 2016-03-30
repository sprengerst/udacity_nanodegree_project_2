/*
 * Created by Stefan Sprenger
 */

package com.sprenger.software.movie.app;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.sprenger.software.movie.app.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DETAIL_LOADER = 0;
    static final String DETAIL_URI = "DETAIL_URI";
    private Uri mUri;
    private ImageView mMoviePosterView;
    private TextView mMovieSynopsisView;
    private TextView mMovieTitleView;
    private TextView mMovieReleaseDateView;
    private TextView mMovieDurationView;
    private RecyclerView movieRecyclerView;
    private TextView mMovieRatingView;

    private TrailerGridAdapter trailerGridAdapter;
    private RecyclerView mMovieReviewRecycler;
    private MaterialFavoriteButton mMovieFavoriteButton;

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
        mMovieReleaseDateView = (TextView) rootView.findViewById(R.id.detail_release_date_textview);
        mMovieDurationView = (TextView) rootView.findViewById(R.id.detail_duration_textview);
        mMovieFavoriteButton = (MaterialFavoriteButton) rootView.findViewById(R.id.detail_favorite_button);
        movieRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_trailers);
        mMovieRatingView = (TextView) rootView.findViewById(R.id.detail_rating_textview);
        mMovieReviewRecycler = (RecyclerView) rootView.findViewById(R.id.recyclerview_reviews);
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
        if (null != mUri) {
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
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        if (data != null && data.moveToFirst()) {

            final String movieId = data.getString(Utility.COL_MOVIE_ID);
            final String movieTitle = data.getString(Utility.COL_MOVIE_TITLE);
            final String moviePopularity = data.getString(Utility.COL_MOVIE_POPUlARITY);
            final String moviePoster = data.getString(Utility.COL_MOVIE_POSTER_PATH);
            final String movieSynopsis = data.getString(Utility.COL_MOVIE_SYNOPSIS);
            final String movieReleaseDate = data.getString(Utility.COL_MOVIE_RELEASE_DATE);
            final String movieRating = data.getString(Utility.COL_MOVIE_RATING);
            final String movieDBID = data.getString(Utility.COL_MOVIE_MOVIEID);


            Picasso
                    .with(getActivity())
                    .load(data.getString(Utility.COL_MOVIE_POSTER_PATH))
                    .into(mMoviePosterView);


            // TODO RATING DURATION
            mMovieTitleView.setText(movieTitle);
            mMovieSynopsisView.setText(movieSynopsis);
            mMovieSynopsisView.setMovementMethod(new ScrollingMovementMethod());
            mMovieReleaseDateView.setText("RELEASE: " + movieReleaseDate);
            mMovieRatingView.setText("RATING: " + movieRating + "/10");

            ArrayList<String> trailerList = new ArrayList<>();

            FetchTrailerDataTask fetchTrailerDataTask = new FetchTrailerDataTask(getContext());
            try {
                trailerList = fetchTrailerDataTask.execute(movieDBID).get();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            ArrayList<String> trailerNameIncremental = new ArrayList<>();

            if (trailerList != null) {
                for (int i = 0; i < trailerList.size(); i++) {
                    trailerNameIncremental.add("Trailer " + (i + 1));
                }

                trailerGridAdapter = new TrailerGridAdapter(getActivity(), trailerNameIncremental, trailerList);
            } else {
                trailerGridAdapter = new TrailerGridAdapter(getActivity(), trailerNameIncremental, new ArrayList<String>());
            }

            movieRecyclerView.setAdapter(trailerGridAdapter);
            LinearLayoutManager horizontalManager
                    = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

            movieRecyclerView.setLayoutManager(horizontalManager);
            movieRecyclerView.setHasFixedSize(true);

            ArrayList<ReviewSpec> reviewSpecs = new ArrayList<>();
            FetchReviewDataTask fetchReviewDataTask = new FetchReviewDataTask(getContext());
            try {
                reviewSpecs = fetchReviewDataTask.execute(movieDBID).get();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            ArrayList<ParentObject> parentObjects = new ArrayList<>();
            for (ReviewSpec reviewSpec : reviewSpecs) {
                ArrayList<Object> childList = new ArrayList<>();
                childList.add(reviewSpec.getReview());
                reviewSpec.setChildObjectList(childList);
                parentObjects.add(reviewSpec);
            }

            ReviewExpandableAdapter reviewExpandableAdapter = new ReviewExpandableAdapter(getActivity(), parentObjects);
            reviewExpandableAdapter.setCustomParentAnimationViewId(R.id.parent_list_item_expand_arrow);
            reviewExpandableAdapter.setParentClickableViewAnimationDefaultDuration();
            reviewExpandableAdapter.setParentAndIconExpandOnClick(true);
            mMovieReviewRecycler.setAdapter(reviewExpandableAdapter);

            LinearLayoutManager verticalManager
                    = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

            verticalManager.setAutoMeasureEnabled(true);
            mMovieReviewRecycler.setLayoutManager(verticalManager);


            mMovieFavoriteButton.setFavorite(data.getInt(Utility.COL_MOVIE_ISFAVORITE) == 1 ? true : false);

            mMovieFavoriteButton.setOnFavoriteChangeListener(
                    new MaterialFavoriteButton.OnFavoriteChangeListener() {
                        @Override
                        public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                            System.out.println("FAVORITE :" + favorite);


                            Uri alterMovie = MovieContract.MovieEntry.buildMovieByMovieId(data.getString(Utility.COL_MOVIE_MOVIEID));

                            ContentValues alteredMovieValues = new ContentValues();
                            alteredMovieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movieTitle);
                            alteredMovieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, movieSynopsis);
                            alteredMovieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, moviePoster);
                            alteredMovieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieReleaseDate);
                            alteredMovieValues.put(MovieContract.MovieEntry.COLUMN_RATING, movieRating);
                            alteredMovieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, moviePopularity);
                            alteredMovieValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, favorite ? 1 : 0);

                            getContext().getContentResolver().update(alterMovie,alteredMovieValues, MovieContract.MovieEntry._ID+"= ?",new String[]{movieId});
                            System.out.println("UPDATED ENTRIES");
                        }
                    });

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
