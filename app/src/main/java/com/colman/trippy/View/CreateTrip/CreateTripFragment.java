package com.colman.trippy.View.CreateTrip;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.colman.trippy.AppConsts;
import com.colman.trippy.Model.Trip;
import com.colman.trippy.Model.TripModel;
import com.colman.trippy.Model.UserModel;
import com.colman.trippy.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateTripFragment extends Fragment {
    EditText mTripName;
    EditText fromDatePicker;
    EditText untilDatePicker;
    ImageView saveTripBtn;
    Button addLocationButton;
    SwitchMaterial privateSwitch;
    Spinner participantsSpinner;
    ChipGroup chipGroup;
    ArrayAdapter<String> participantsAdapter;
    Calendar calendar = Calendar.getInstance();
    String[] allEmails;

    ArrayList<String> participantsEmails;
    long fromDate;
    boolean isSpinnerFirstCall;
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
        saveTripBtn = view.findViewById(R.id.save_button);
        participantsSpinner = view.findViewById(R.id.participants_spinner);
        chipGroup = view.findViewById(R.id.participants_list);
        isSpinnerFirstCall = true;

        participantsEmails = new ArrayList<>();
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

        UserModel.instance.getAllUserEmails(new AppConsts.Listener<String[]>() {
            @Override
            public void onComplete(String[] result) {
                allEmails = result;
                participantsAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, result);
                participantsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                participantsSpinner.setAdapter(participantsAdapter);
            }

            @Override
            public void onFailure(String message) {

            }
        });

        participantsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!isSpinnerFirstCall) {
                    String selectedEmail = allEmails[i];
                    if (!participantsEmails.contains(selectedEmail)) {
                        participantsEmails.add(selectedEmail);
                        chipGroup.addView(createEmailChip(selectedEmail));
                    } else {
                        Toast.makeText(getContext(), "You already chose this e-mail", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    isSpinnerFirstCall = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        saveTripBtn.setOnClickListener(view12 -> {

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
                    adapter.getLocations()), () -> Navigation.findNavController(saveTripBtn).popBackStack());
        });

        handleRecyclerView(view, adapter);
        return view;
    }

    private Chip createEmailChip(String selectedEmail) {
        Chip chip = new Chip(getContext());
        chip.setText(selectedEmail);
        chip.setChipBackgroundColorResource(R.color.primary_color);
        chip.setCloseIconVisible(true);
        chip.setTextColor(getResources().getColor(R.color.white));
        return chip;
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
