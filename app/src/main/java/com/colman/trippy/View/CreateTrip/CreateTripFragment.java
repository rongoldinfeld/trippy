package com.colman.trippy.View.CreateTrip;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.colman.trippy.AppConsts;
import com.colman.trippy.Model.Location;
import com.colman.trippy.Model.Trip;
import com.colman.trippy.Model.TripModel;
import com.colman.trippy.Model.User;
import com.colman.trippy.Model.UserModel;
import com.colman.trippy.R;
import com.colman.trippy.View.TripDetails.TripDetailsFragmentArgs;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.stream.Collectors;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class CreateTripFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener, AppConsts.Listener<String[]> {
    EditText mTripName;
    EditText fromDatePicker;
    EditText untilDatePicker;
    ImageView saveTripBtn;
    Button addLocationButton;
    SwitchMaterial privateSwitch;
    Spinner participantsSpinner;
    ChipGroup chipGroup;
    ProgressBar participantsPb;
    ProgressBar saveTripPb;
    ArrayAdapter<String> participantsAdapter;
    Calendar calendar = Calendar.getInstance();
    String[] allEmails;
    LocationsListAdapter adapter;
    RecyclerView rv;
    Trip initialTrip;

    ArrayList<String> participantsEmails;
    long fromDate;
    boolean isSpinnerFirstCall;
    long untilDate;


    interface OnDateChangeListener {
        void onChange(long date);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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
        participantsPb = view.findViewById(R.id.participants_pb);
        saveTripPb = view.findViewById(R.id.save_trip_pb);
        isSpinnerFirstCall = true;

        participantsEmails = new ArrayList<>();
        adapter = new LocationsListAdapter(CreateTripFragment.this);
        fromDatePicker.setOnClickListener(datePickerView -> showDatePickerDialog(fromDatePicker, null, (long date) -> fromDate = date));
        untilDatePicker.setOnClickListener(datePickerView -> {
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

        participantsPb.setVisibility(View.VISIBLE);
        UserModel.instance.getAllUserEmails(this);
        participantsSpinner.setOnItemSelectedListener(this);
        saveTripBtn.setOnClickListener(this);
        handleRecyclerView(view, adapter);

        this.initialTrip = CreateTripFragmentArgs.fromBundle(getArguments()).getTripInfo();
        if (this.initialTrip != null) {
            this.populateTripForm(this.initialTrip);
        }

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void populateTripForm(Trip trip) {
        mTripName.setText(trip.getName());
        privateSwitch.setChecked(trip.isTripPrivate());
        fromDatePicker.setText(AppConsts.sdf.format(trip.getFromDate()));
        fromDate = trip.getFromDate();
        untilDatePicker.setText(AppConsts.sdf.format(trip.getUntilDate()));
        untilDate = trip.getUntilDate();
        adapter.setLocations(trip.getLocations());
        adapter.notifyDataSetChanged();
        trip.getParticipantsEmails().forEach(email -> {
            participantsEmails.add(email);
            chipGroup.addView(createEmailChip(email));
        });
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("ClickableViewAccessibility")
    private Chip createEmailChip(String selectedEmail) {
        Chip chip = new Chip(getContext());
        chip.setText(selectedEmail);
        chip.setChipBackgroundColorResource(R.color.primary_color);
        chip.setCloseIconVisible(true);
        chip.setTextColor(getResources().getColor(R.color.white));
        chip.setOnTouchListener((view, motionEvent) -> {
            participantsEmails.remove(selectedEmail);
            chipGroup.removeView(chip);
            return false;
        });
        return chip;
    }

    @SuppressLint("ClickableViewAccessibility")
    private LocationsListAdapter handleRecyclerView(View view, LocationsListAdapter adapter) {
        this.rv = view.findViewById(R.id.locations_rv);
        this.rv.setAdapter(adapter);
        this.rv.setLayoutManager(new LinearLayoutManager(getContext()));
        this.rv.setOnTouchListener((v, event) -> {
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (!isSpinnerFirstCall) {
            if (i > 0) {
                String selectedEmail = allEmails[i - 1];
                if (!participantsEmails.contains(selectedEmail)) {
                    participantsEmails.add(selectedEmail);
                    chipGroup.addView(createEmailChip(selectedEmail));
                } else {
                    Toast.makeText(getContext(), "You already chose this e-mail", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            isSpinnerFirstCall = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        this.toggleCreateProgressBar(true);
        if (mTripName.getText().toString().trim().length() == 0) {
            mTripName.setError("You must enter trip name!");
            this.toggleCreateProgressBar(false);
            return;
        }

        if (fromDate == 0L) {
            fromDatePicker.setError("Please your trip from date!");
            this.toggleCreateProgressBar(false);
            return;
        }

        if (untilDate == 0L) {
            untilDatePicker.setError("Please your trip until date!");
            this.toggleCreateProgressBar(false);
            return;
        }


        boolean isAllImagesEmpty = !adapter.getLocations().stream().anyMatch(location -> !TextUtils.equals(location.getImageUrl(), ""));
        if (!isAllImagesEmpty) {
            TripModel.instance.uploadImages(adapter.getLocations(), new AppConsts.Listener<ArrayList<String>>() {
                @Override
                public void onComplete(ArrayList<String> result) {
                    Trip tripToCreateOrUpdate = new Trip(
                            participantsEmails,
                            mTripName.getText().toString().trim(),
                            fromDate,
                            untilDate,
                            privateSwitch.isChecked(),
                            adapter.getLocations(),
                            true);
                    if (initialTrip == null) {
                        TripModel.instance.addTrip(tripToCreateOrUpdate, () -> Navigation.findNavController(saveTripBtn).popBackStack());
                    } else {
                        tripToCreateOrUpdate.setDataVersion(initialTrip.getDataVersion() + 1);
                        TripModel.instance.updateTrip(initialTrip, tripToCreateOrUpdate, () -> Navigation.findNavController(saveTripBtn).navigate(R.id.action_createTrip_to_userProfileFragment));
                    }
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(getContext(), "Failed to upload your images..." + message, Toast.LENGTH_LONG).show();
                    toggleCreateProgressBar(false);
                }
            });
        } else {
            ArrayList<Location> locations = adapter.getLocations();
            Trip tripToCreateOrUpdate = new Trip(
                    participantsEmails,
                    mTripName.getText().toString().trim(),
                    fromDate,
                    untilDate,
                    privateSwitch.isChecked(),
                    locations,
                    true);
            if (initialTrip == null) {
                TripModel.instance.addTrip(tripToCreateOrUpdate, () -> Navigation.findNavController(saveTripBtn).popBackStack());
            } else {
                tripToCreateOrUpdate.setDataVersion(initialTrip.getDataVersion() + 1);
                TripModel.instance.updateTrip(initialTrip, tripToCreateOrUpdate, () -> Navigation.findNavController(saveTripBtn).navigate(R.id.action_createTrip_to_userProfileFragment));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onComplete(String[] result) {
        UserModel.instance.getCurrentUser(new AppConsts.Listener<User>() {
            @Override
            public void onComplete(User currentUser) {
                allEmails = Arrays.stream(result).filter(email -> !TextUtils.equals(email, currentUser.getEmail())).toArray(String[]::new);
                String[] options = add2BeginningOfArray(allEmails, "Select participants!");
                participantsAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, options);
                participantsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                participantsSpinner.setAdapter(participantsAdapter);
                participantsPb.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(String message) {
                participantsPb.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "Failed to load user mails!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFailure(String message) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public <T> T[] add2BeginningOfArray(T[] elements, T element) {
        T[] newArray = Arrays.copyOf(elements, elements.length + 1);
        newArray[0] = element;
        System.arraycopy(elements, 0, newArray, 1, elements.length);

        return newArray;
    }

    @Override
    public void onActivityResult(int position, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                if (selectedImage != null) {
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        LocationsListAdapter.ViewHolder viewHolder = (LocationsListAdapter.ViewHolder) this.rv.findViewHolderForAdapterPosition(position);
                        if (viewHolder != null) {
                            adapter.setImage(viewHolder, position, picturePath);
                        }
                        cursor.close();
                    }
                }
            }
        }
    }

    private void toggleCreateProgressBar(boolean on) {
        if(on) {
            saveTripPb.setVisibility(View.VISIBLE);
            saveTripBtn.setVisibility(View.INVISIBLE);
        } else {
            saveTripPb.setVisibility(View.INVISIBLE);
            saveTripBtn.setVisibility(View.VISIBLE);
        }
    }
}
