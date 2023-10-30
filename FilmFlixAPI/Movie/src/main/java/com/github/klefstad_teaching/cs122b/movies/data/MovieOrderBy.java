package com.github.klefstad_teaching.cs122b.movies.data;

import java.util.Locale;


/*
    orderBy	String	Sorting parameter: title (default) or rating or year
    direction	String	Sorting direction: asc (default) or desc
 */
public enum MovieOrderBy
{
    TITLE(" ORDER BY m.title , m.id "),// Default
    RATING(" ORDER BY m.rating , m.id "),
    YEAR(" ORDER BY m.year , m.id "),

    TITLE_DESC(" ORDER BY m.title DESC, m.id "),// Default
    RATING_DESC(" ORDER BY m.rating DESC, m.id"),
    YEAR_DESC(" ORDER BY m.year DESC, m.id ");

    private final String sql;

    MovieOrderBy(String sql)
    {
        this.sql = sql;
    }

    public String toSql()
    {
        return sql;
    }

    public static MovieOrderBy fromString(String orderBy,String direction)
    {
        String temp = orderBy.toUpperCase(Locale.ROOT) + " "+direction.toUpperCase(Locale.ROOT);
        switch (temp)
        {
            case "TITLE ASC":
                return TITLE;
            case "RATING ASC":
                return RATING;
            case "YEAR ASC":
                return YEAR;
            case "TITLE DESC":
                return TITLE_DESC;
            case "RATING DESC":
                return RATING_DESC;
            case "YEAR DESC":
                return YEAR_DESC;
            default:
                throw new RuntimeException("No MovieOrderBy value for: " + temp);
        }
    }
}
