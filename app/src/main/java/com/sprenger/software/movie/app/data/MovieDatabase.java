package com.sprenger.software.movie.app.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 *
 * Created by Stefan Sprenger
 *
 */

@Database(version = MovieDatabase.VERSION)
public final class MovieDatabase {
    private MovieDatabase(){}

    public static final int VERSION = 4;

        @Table(MovieColumns.class) public static final String MOVIES = "movies";

}
