package com.colman.trippy.View.CreateTrip;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.colman.trippy.AppConsts;
import com.colman.trippy.Model.Location;
import com.colman.trippy.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.colman.trippy.AppConsts.getLongFromDate;
import static com.colman.trippy.AppConsts.sdf;

public class LocationsListAdapter extends RecyclerView.Adapter<LocationsListAdapter.ViewHolder> {
    private final Fragment mFragment;
    Calendar calendar = Calendar.getInstance();

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final Fragment mFragment;
        public EditText locationName, locationDate;
        public Button editImageButton;
        public ImageView image;
        public ImageView removeLocationIcon;

        public ViewHolder(View view, Fragment mFragment) {
            super(view);
            this.mFragment = mFragment;
            locationName = view.findViewById(R.id.location_text);
            locationDate = view.findViewById(R.id.location_date);
            editImageButton = view.findViewById(R.id.edit_image_button);
            image = view.findViewById(R.id.image_view);
            removeLocationIcon = view.findViewById(R.id.remove_location_icon);

        }
    }

    private ArrayList<Location> locationDataSet;

    private long minDate;
    private long maxDate;

    public LocationsListAdapter(Fragment fragment) {
        this.locationDataSet = new ArrayList<>();
        this.mFragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_item, viewGroup, false);

        return new ViewHolder(view, mFragment);
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
                if (position < locationDataSet.size()) {
                    locationDataSet.get(position).setLocationName(editable.toString());
                }
            }
        });

        viewHolder.editImageButton.setOnClickListener(view -> {
            final CharSequence[] options = {"Choose from Gallery", "Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Choose your profile picture");
            builder.setItems(options, (dialog, item) -> {
                if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    mFragment.startActivityForResult(pickPhoto, position);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            });
            builder.show();
        });

        String locationImageUrl = locationDataSet.get(position).getImageUrl();
        if (!TextUtils.equals(locationImageUrl, "")) {
            AppConsts.loadPicture(locationImageUrl, 100, 100, viewHolder.image);
            viewHolder.image.setVisibility(View.VISIBLE);
        }

        viewHolder.removeLocationIcon.setOnClickListener(view -> {
            if (position < locationDataSet.size()) {
                locationDataSet.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            } else {
                Log.d("TRIPLOG", "Tried to delete location of non existing index: " + position + " array size is: " + locationDataSet.size());
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

    public void addNewEmptyLocation() {
        locationDataSet.add(new Location(minDate, "", ""));
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

    public void setLocations(ArrayList<Location> newList) {
        this.locationDataSet = newList;
    }

    public void setImage(ViewHolder viewHolder, int position, String picturePath) {
        if (!TextUtils.equals(picturePath, "")) {
            this.locationDataSet.get(position).setImageUrl(picturePath);
            if (!picturePath.contains("firebase")) {
                picturePath = "file://" + picturePath;
            }

            AppConsts.loadPicture(picturePath, 100, 100, viewHolder.image);
            viewHolder.image.setVisibility(View.VISIBLE);
        }
    }
}
