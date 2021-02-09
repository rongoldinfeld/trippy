package com.colman.trippy.View.CreateTrip;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.colman.trippy.AppConsts;
import com.colman.trippy.Model.Trip;
import com.colman.trippy.Model.TripModel;
import com.colman.trippy.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateTripFragment extends Fragment {
    EditText mTripName;
    EditText fromDatePicker;
    EditText untilDatePicker;
    ImageView save_trip_button;
    Button addLocationButton;
    SwitchMaterial privateSwitch;
    Calendar calendar = Calendar.getInstance();

    long fromDate;
    long untilDate;

    interface OnDateChangeListener {
        void onChange(long date);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_trip, container, false);
        mTripName = view.findViewById(R.id.trip_name_text);
        fromDatePicker = view.findViewById(R.id.trip_from_date);
        untilDatePicker = view.findViewById(R.id.trip_until_date);
        privateSwitch = view.findViewById(R.id.private_switch);
        save_trip_button = view.findViewById(R.id.save_button);

        LocationsListAdapter adapter = new LocationsListAdapter();
        fromDatePicker.setOnClickListener(view1 -> showDatePickerDialog(fromDatePicker, null, (long date) -> fromDate = date));
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

        save_trip_button.setOnClickListener(view12 -> {

            if (mTripName.getText().toString().trim().length() == 0) {
                mTripName.setError("You must enter trip name!");
                return;
            }

            if (fromDate == 0L) {
                fromDatePicker.setError("Please your trip from date!");
                return;
            }

            if (untilDate == 0L) {
                untilDatePicker.setError("Please your trip until date!");
                return;
            }

            TripModel.instance.addTrip(new Trip(
                    new ArrayList<>(0),
                    mTripName.getText().toString().trim(),
                    fromDate,
                    untilDate,
                    privateSwitch.isChecked(),
                    adapter.getLocations()));
        });

        handleRecyclerView(view, adapter);
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
                adapter.addNewEmptyLocation();
                adapter.notifyItemInserted(adapter.getItemCount());
            }
        });

        return adapter;
    }

    public void showDatePickerDialog(EditText container, Long minDate, OnDateChangeListener listener) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            container.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
            listener.onChange(AppConsts.getLongFromDate(year, month, dayOfMonth));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        if (minDate != null) {
            datePickerDialog.getDatePicker().setMinDate(minDate);
        }
        datePickerDialog.show();
    }
}
