package com.colman.trippy;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

public class CreateTripFragment extends Fragment {
    EditText fromDatePicker;
    EditText untilDatePicker;
    Button addLocationButton;
    Calendar calendar;
    int defaultYear, defaultMonth, defaultDay;

    Long fromDate = 0L;
    Long untilDate = 0L;

    interface OnDateChangeListener {
        void onChange(long date);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_trip, container, false);
        setDefaultCalendarValues();

        Toolbar toolbar = getActivity().findViewById(R.id.app_main_toolbar);
        toolbar.inflateMenu(R.menu.create_trip_action_bar_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            Log.d("TRIPLOG", "CLICKED ON TOOLBAR" + item.getItemId());
            return false;
        });

        LocationsListAdapter adapter = new LocationsListAdapter();
        handleRecyclerView(view, adapter);

        fromDatePicker = view.findViewById(R.id.trip_from_date);
        fromDatePicker.setOnClickListener(view1 -> showDatePickerDialog(fromDatePicker, null, (long date) -> fromDate = date));
        untilDatePicker = view.findViewById(R.id.trip_until_date);
        untilDatePicker.setOnClickListener(view1 -> {
            if (fromDate == 0L) {
                Toast.makeText(getContext(), "You must first provide from date", Toast.LENGTH_SHORT).show();
            } else {
                showDatePickerDialog(untilDatePicker, fromDate, (long date) -> {
                    untilDate = date;
                    adapter.setMaxDate(untilDate);
                    adapter.setMinDate(fromDate);
                });
            }
        });

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private LocationsListAdapter handleRecyclerView(View view, LocationsListAdapter adapter) {
        RecyclerView rv = view.findViewById(R.id.locations_rv);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setOnTouchListener((v, event) -> {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return false;
        });

        addLocationButton = view.findViewById(R.id.add_location_button);
        addLocationButton.setOnClickListener(button -> {
            if (fromDate == 0L || untilDate == 0L) {
                Toast.makeText(getContext(), "You must first provide from/until date", Toast.LENGTH_SHORT).show();
            } else {
                adapter.addNewLocation();
                adapter.notifyItemInserted(adapter.getItemCount());
            }
        });

        return adapter;
    }

    private void setDefaultCalendarValues() {
        calendar = Calendar.getInstance();
        defaultYear = calendar.get(Calendar.YEAR);
        defaultMonth = calendar.get(Calendar.MONTH);
        defaultDay = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public void showDatePickerDialog(EditText container, Long minDate, OnDateChangeListener listener) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            container.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
            listener.onChange(AppConsts.getLongFromDate(year, month, dayOfMonth));
        }, defaultYear, defaultMonth, defaultDay);

        if (minDate != null) {
            datePickerDialog.getDatePicker().setMinDate(minDate);
        }
        datePickerDialog.show();
    }
}
