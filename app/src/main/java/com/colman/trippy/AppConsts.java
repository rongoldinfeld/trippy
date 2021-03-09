package com.colman.trippy;

import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

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

    public interface OnCompleteListener {
        void onComplete();
    }

    public static void loadPicture(String url, int width, int height, ImageView container) {
        Picasso.get().load(url).networkPolicy(NetworkPolicy.OFFLINE).resize(width, height).into(container, new Callback() {
            @Override
            public void onSuccess() {
                Log.d("TRIPLOG", "Image with url: " + url.substring(44) + " was loaded from cache");
            }

            @Override
            public void onError(Exception e) {
                // Try again online if cache failed
                Log.d("TRIPLOG", "Couldn't load image " + url.substring(44) + " from cache, loading a from firebase");
                Picasso.get().load(url).placeholder(R.drawable.ic_image_placeholder).resize(width, height).into(container);
            }
        });

    }
}
