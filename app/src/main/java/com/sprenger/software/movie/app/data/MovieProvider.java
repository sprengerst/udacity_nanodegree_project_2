package com.sprenger.software.movie.app.data;

import android.content.ContentResolver;
import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 *
 * Created by Stefan Sprenger
 *
 */

@ContentProvider(authority = MovieProvider.AUTHORITY, database = MovieDatabase.class)
public final class MovieProvider {
    public static final String AUTHORITY =
            "com.sprenger.software.movie.app.data.MovieProvider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path{
        String MOVIES = "movies";
        String ARCHIVED_MOVIES ="archived_movies";
    }

    private static Uri buildUri(String... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths){
            builder.appendPath(path);
        }
        return builder.build();
    }
    @TableEndpoint(table = MovieDatabase.MOVIES) public static class Movies{
        @ContentUri(
                path = Path.MOVIES,
                type = ContentResolver.CURSOR_DIR_BASE_TYPE+"/movie",
                defaultSort = MovieColumns.RATING + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.MOVIES);

        @InexactContentUri(
                name = "MOVIE_ID",
                path = Path.MOVIES + "/#",
                type = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/movie",
                whereColumn = MovieColumns._ID,
                pathSegment = 1)
        public static Uri withId(long id){
            return buildUri(Path.MOVIES, String.valueOf(id));
        }
    }
}
