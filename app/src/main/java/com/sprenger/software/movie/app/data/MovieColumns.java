package com.sprenger.software.movie.app.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 *
 * Created by Stefan Sprenger
 *
 */
public interface MovieColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String _ID =
            "_id";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String TITLE = "title";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String POSTERPATH = "posterPath";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String SYNOPSIS = "synopsis";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String RELEASEDATE = "releasedate";

    @DataType(DataType.Type.REAL) @NotNull
    public static final String POPULARITY = "popularity";

    @DataType(DataType.Type.REAL) @NotNull
    public static final String RATING = "rating";

}

