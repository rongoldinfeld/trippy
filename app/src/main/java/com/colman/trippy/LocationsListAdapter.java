package com.colman.trippy;

import android.app.DatePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.colman.trippy.models.Location;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.colman.trippy.AppConsts.getLongFromDate;
import static com.colman.trippy.AppConsts.sdf;

public class LocationsListAdapter extends RecyclerView.Adapter<LocationsListAdapter.ViewHolder> {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public EditText locationName, locationDate;
        Calendar calendar = Calendar.getInstance();
        private final int defaultYear = calendar.get(Calendar.YEAR);
        private final int defaultMonth = calendar.get(Calendar.MONTH);
        private final int defaultDay = calendar.get(Calendar.DAY_OF_MONTH);
        private long minimumDate = 0L;
        private long maxmimumDate = 0L;

        public ViewHolder(View view) {
            super(view);
            locationName = view.findViewById(R.id.location_text);
            locationDate = view.findViewById(R.id.location_date);
            locationDate.setOnClickListener(view12 -> openDatePickerDialog(view));
        }

        private void openDatePickerDialog(View view) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), (view1, year, month, dayOfMonth) -> {
                String formattedDateVisited = sdf.format(getLongFromDate(year, month, dayOfMonth));
                locationDate.setText(formattedDateVisited);
            }, defaultYear, defaultMonth, defaultDay);

            if (minimumDate != 0L) {
                datePickerDialog.getDatePicker().setMinDate(minimumDate);
            }

            if (maxmimumDate != 0L) {
                datePickerDialog.getDatePicker().setMaxDate(maxmimumDate);
            }
            datePickerDialog.show();
        }

        public void setMinDate(long date) {
            minimumDate = date;
        }

        public void setMaxDate(long date) {
            maxmimumDate = date;
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
        viewHolder.setMinDate(minDate);
        viewHolder.setMaxDate(maxDate);
        String formattedDateVisited = sdf.format(new Date(locationDataSet.get(position).getDateVisited()));
        viewHolder.locationDate.setText(formattedDateVisited);
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
}
