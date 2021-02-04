package com.colman.trippy.View.CreateTrip;

import android.app.DatePickerDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.colman.trippy.Model.Location;
import com.colman.trippy.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.colman.trippy.AppConsts.getLongFromDate;
import static com.colman.trippy.AppConsts.sdf;

public class LocationsListAdapter extends RecyclerView.Adapter<LocationsListAdapter.ViewHolder> {
    Calendar calendar = Calendar.getInstance();

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public EditText locationName, locationDate;

        public ViewHolder(View view) {
            super(view);
            locationName = view.findViewById(R.id.location_text);
            locationDate = view.findViewById(R.id.location_date);
        }
    }

    private final ArrayList<Location> locationDataSet;
    private long minDate;
    private long maxDate;

    public LocationsListAdapter() {
        this.locationDataSet = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.locationName.setText(locationDataSet.get(position).getLocationName());
        String formattedDateVisited = sdf.format(new Date(locationDataSet.get(position).getDateVisited()));
        viewHolder.locationDate.setText(formattedDateVisited);

        viewHolder.locationDate.setOnClickListener(view -> openDatePickerDialog(view, viewHolder.locationDate, position));
        viewHolder.locationName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                locationDataSet.get(position).setLocationName(editable.toString());
            }
        });
    }

    private void openDatePickerDialog(View view, EditText mField, int position) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), (view1, year, month, dayOfMonth) -> {
            long longDate = getLongFromDate(year, month, dayOfMonth);
            String formattedDateVisited = sdf.format(longDate);
            locationDataSet.get(position).setDateVisited(longDate);
            mField.setText(formattedDateVisited);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        if (minDate != 0L) {
            datePickerDialog.getDatePicker().setMinDate(minDate);
        }

        if (maxDate != 0L) {
            datePickerDialog.getDatePicker().setMaxDate(maxDate);
        }
        datePickerDialog.show();
    }

    @Override
    public int getItemCount() {
        return locationDataSet.size();
    }

    public void addNewLocation() {
        locationDataSet.add(Location.generateEmptyLocation());
    }

    public void setMinDate(long date) {
        this.minDate = date;
    }

    public void setMaxDate(long date) {
        this.maxDate = date;
    }

    public ArrayList<Location> getLocations() {
        return this.locationDataSet;
    }
}
