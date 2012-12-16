package com.teemtok.yamba;

import android.content.SearchRecentSuggestionsProvider;

public class MySuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.teemtok.yamba.MySuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES | DATABASE_MODE_2LINES;;

    public MySuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
