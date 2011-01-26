package com.tj.qotd;

import android.provider.BaseColumns;

public final class Quote implements BaseColumns {

     //This class cannot be instanciated
    private Quote() {}

    /**
     * The quote itself
     * <p>type : TEXT</p>
     */
    public static final String QUOTE = "quote";

    /**
     * The Quote author
     * <p>type: TEXT</p>
     */
    public static final String AUTHOR = "author";
}
