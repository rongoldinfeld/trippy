package com.colman.trippy;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public final class AppConsts {
    public static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM", new Locale("he", "IL"));

    public static final long getLongFromDate(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, 0, 0);
        return c.getTimeInMillis();
    }

    public interface Listener<T> {

        void onComplete(T result);

        void onFailure(String message);

    }
}
