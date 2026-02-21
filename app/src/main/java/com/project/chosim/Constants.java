package com.project.chosim;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd.", Locale.US);

    public static final int SEARCH_RESULT_ITEM_COUNT_PER_PAGE = 10;
    public static final int SEARCH_RESULT_MAXIMUM_PAGE_NUMBER_COUNT = 5;

    // Firestore path
    public static String USERS = "users";
    public static String ROUTINES = "routines";
    public static String DONE = "done";
    public static String RATE = "rate";
}
