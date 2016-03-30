/*
 * Created by Stefan Sprenger
 */

package com.sprenger.software.movie.app;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.sprenger.software.movie.app.configuration.ReviewExpandableAdapter;
import com.sprenger.software.movie.app.configuration.ReviewSpec;
import com.sprenger.software.movie.app.configuration.TrailerGridAdapter;
import com.sprenger.software.movie.app.database.MovieContract;
import com.sprenger.software.movie.app.download.FetchReviewDataTask;
import com.sprenger.software.movie.app.download.FetchTrailerDataTask;
import com.sprenger.software.movie.app.utilities.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DETAIL_LOADER = 111;
    static final String DETAIL_URI = "DETAIL_URI";
    private Uri mUri;

    private ImageView mMoviePosterView;
    private TextView mMovieSynopsisView;
    private TextView mMovieTitleView;
    private TextView mMovieReleaseDateView;
    private RecyclerView movieRecyclerView;
    private TextView mMovieRatingView;
    private RecyclerView mMovieReviewRecycler;
    private ImageButton mMovieFavoriteButton;
    private TextView mMovieReviewHeadline;
    private TextView mMovieTrailerHeadline;
    private ShareActionProvider mShareActionProvider;
    private String mFirstTrailerYTID;

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mFirstTrailerYTID != null) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
            System.out.println("DETAIL RECEIVED URI: " + mUri);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mMoviePosterView = (ImageView) rootView.findViewById(R.id.image_view_detail);
        mMovieSynopsisView = (TextView) rootView.findViewById(R.id.synopsis_text_detail);
        mMovieTitleView = (TextView) rootView.findViewById(R.id.title_text_detail);
        mMovieReleaseDateView = (TextView) rootView.findViewById(R.id.detail_release_date_textview);
        mMovieFavoriteButton = (ImageButton) rootView.findViewById(R.id.detail_favorite_button);
        movieRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_trailers);
        mMovieRatingView = (TextView) rootView.findViewById(R.id.detail_rating_textview);
        mMovieReviewRecycler = (RecyclerView) rootView.findViewById(R.id.recyclerview_reviews);
        mMovieReviewHeadline = (TextView) rootView.findViewById(R.id.textview_review_headlines);
        mMovieTrailerHeadline = (TextView) rootView.findViewById(R.id.textview_trailer_headlines);


        //set to invisible if there is no content
        setComponentsVisibility(View.INVISIBLE);

        return rootView;
    }

    private void setComponentsVisibility(int visibilityStatus) {
        mMoviePosterView.setVisibility(visibilityStatus);
        mMovieSynopsisView.setVisibility(visibilityStatus);
        mMovieTitleView.setVisibility(visibilityStatus);
        mMovieReleaseDateView.setVisibility(visibilityStatus);
        mMovieFavoriteButton.setVisibility(visibilityStatus);
        movieRecyclerView.setVisibility(visibilityStatus);
        mMovieRatingView.setVisibility(visibilityStatus);
        mMovieReviewRecycler.setVisibility(visibilityStatus);
        mMovieReviewHeadline.setVisibility(visibilityStatus);
        mMovieTrailerHeadline.setVisibility(visibilityStatus);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
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

            // content fills the components now
            setComponentsVisibility(View.VISIBLE);

            final String movieId = data.getString(Utility.COL_MOVIE_ID);

            final String movieTitle = data.getString(Utility.COL_MOVIE_TITLE);
            final String moviePopularity = data.getString(Utility.COL_MOVIE_POPUlARITY);
            final String moviePoster = data.getString(Utility.COL_MOVIE_POSTER_PATH);
            final String movieSynopsis = data.getString(Utility.COL_MOVIE_SYNOPSIS);
            final String movieReleaseDate = data.getString(Utility.COL_MOVIE_RELEASE_DATE);
            final String movieRating = data.getString(Utility.COL_MOVIE_RATING);
            final String mMovieDBID = data.getString(Utility.COL_MOVIE_MOVIEDBID);

            Picasso
                    .with(getActivity())
                    .load(data.getString(Utility.COL_MOVIE_POSTER_PATH))
                    .into(mMoviePosterView);


            mMovieTitleView.setText(movieTitle);
            mMovieSynopsisView.setText(movieSynopsis);
            mMovieSynopsisView.setMovementMethod(new ScrollingMovementMethod());

            mMovieReleaseDateView.setText(String.format(getString(R.string.release_text), movieReleaseDate));
            mMovieRatingView.setText(String.format(getString(R.string.rating_text),movieRating));

            mMovieFavoriteButton.setSelected(data.getInt(Utility.COL_MOVIE_ISFAVORITE) == 1);
            mMovieFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mMovieFavoriteButton.setSelected(!mMovieFavoriteButton.isSelected());

                    Uri alterMovie = MovieContract.MovieEntry.buildMovieByMovieId(data.getString(Utility.COL_MOVIE_MOVIEDBID));
                    ContentValues alteredMovieValues = new ContentValues();
                    alteredMovieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movieTitle);
                    alteredMovieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, movieSynopsis);
                    alteredMovieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, moviePoster);
                    alteredMovieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieReleaseDate);
                    alteredMovieValues.put(MovieContract.MovieEntry.COLUMN_RATING, movieRating);
                    alteredMovieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, moviePopularity);
                    alteredMovieValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, mMovieFavoriteButton.isSelected() ? 1 : 0);

                    getContext().getContentResolver().update(alterMovie, alteredMovieValues, MovieContract.MovieEntry._ID + "= ?", new String[]{movieId});

                    if (Utility.getOnlyFavoriteOption(getContext()) && !mMovieFavoriteButton.isSelected()) {
                        updateLoadedEntry();
                    }
                }
            });

            if (Utility.isNetworkAvailable(getContext())) {


                ArrayList<String> trailerList = new ArrayList<>();

                FetchTrailerDataTask fetchTrailerDataTask = new FetchTrailerDataTask(getContext());
                try {
                    trailerList = fetchTrailerDataTask.execute(mMovieDBID).get();

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                ArrayList<String> trailerNameIncremental = new ArrayList<>();

                TrailerGridAdapter trailerGridAdapter;
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
                    reviewSpecs = fetchReviewDataTask.execute(mMovieDBID).get();

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }


                if(trailerList != null && trailerList.size()>0) {
                    mFirstTrailerYTID = trailerList.get(0);
                    if (mShareActionProvider != null) {
                        mShareActionProvider.setShareIntent(createShareTrailerIntent());
                    }
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

            } else {
                Utility.showToast("To enable movie trailers/reviews check your network connection", getContext());
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void updateLoadedEntry() {
        if (null != mUri) {

            Cursor topEntry;
            String selector = null;
            String[] selectorValues = null;

            if (Utility.getOnlyFavoriteOption(getContext())) {
                selector = MovieContract.MovieEntry.COLUMN_IS_FAVORITE + " = ?";
                selectorValues = new String[]{"1"};
            }

            topEntry = getContext().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    selector,
                    selectorValues,
                    Utility.getSortOrderSQL(Utility.getPreferedSortOrder(getContext())));

            if (topEntry != null && topEntry.moveToFirst()) {
                mUri = MovieContract.MovieEntry.buildMovieByMovieId(topEntry.getString(Utility.COL_MOVIE_MOVIEDBID));
                topEntry.close();
            }


            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    private Intent createShareTrailerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        }else{
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }

        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=" + mFirstTrailerYTID);
        return shareIntent;
    }
}
