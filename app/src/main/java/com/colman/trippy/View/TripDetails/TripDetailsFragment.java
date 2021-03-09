package com.colman.trippy.View.TripDetails;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.colman.trippy.Model.Location;
import com.colman.trippy.Model.Trip;
import com.colman.trippy.Model.TripModel;
import com.colman.trippy.R;
import com.colman.trippy.View.Home.Search.TripSearchFragmentDirections;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.colman.trippy.AppConsts.sdf;

public class TripDetailsFragment extends Fragment {
    RecyclerView recyclerView;
    TripDetailsListAdapter adapter;
    View detailsViewFragment;
    Trip trip;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        detailsViewFragment = inflater.inflate(R.layout.fragment_trip_details, container, false);
        trip = TripDetailsFragmentArgs.fromBundle(getArguments()).getTrip();

        TextView tripName = detailsViewFragment.findViewById(R.id.trip_details_name);
        tripName.setText(trip.getName());

        TextView datesView = detailsViewFragment.findViewById(R.id.trip_detail_dates);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String fromDate = sdf.format(trip.getFromDate());
        String untilDate = sdf.format(trip.getUntilDate());
        datesView.setText(String.format("%s - %s", fromDate, untilDate));

        TextView participantsView = detailsViewFragment.findViewById(R.id.trip_detail_participants);
        if (trip.getParticipantsEmails().size() > 1) {
            participantsView.setText(trip.getParticipantsEmails().get(0).substring(0, 3).concat("... +" + (trip.getParticipantsEmails().size() - 1)));
        } else {
            participantsView.setText(trip.getParticipantsEmails().stream().collect(Collectors.joining(",")));
        }

        ImageView privateLockView = detailsViewFragment.findViewById(R.id.trip_details_private_lock);
        if (trip.isTripPrivate()) {
            privateLockView.setVisibility(View.VISIBLE);
        }

        ImageView editButton = detailsViewFragment.findViewById(R.id.trip_details_edit);

        ImageView deleteButton = detailsViewFragment.findViewById(R.id.trip_details_delete);
        deleteButton.setOnClickListener(imageView -> TripModel.instance.removeTrip(trip, () -> {
            Log.d("TRIPLOG", "trip named " + trip.getName() + " deleted");
            Navigation.findNavController(deleteButton).popBackStack();
        }));

        if (trip.isCurrentUser()) {
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        }

        editButton.setOnClickListener(view -> {
            TripDetailsFragmentDirections.ActionTripDetailsFragmentToCreateTrip action = TripDetailsFragmentDirections.actionTripDetailsFragmentToCreateTrip().setTripInfo(trip);            Navigation.findNavController(detailsViewFragment).navigate(action);
        });

        recyclerView = detailsViewFragment.findViewById(R.id.trip_detail_location_list);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TripDetailsFragment.TripDetailsListAdapter();
        recyclerView.setAdapter(adapter);

        return detailsViewFragment;
    }

    class TripDetailsListAdapter extends RecyclerView.Adapter<LocationItemViewHolder> {
        @NonNull
        @Override
        public LocationItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_location_details, parent, false);
            return new LocationItemViewHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onBindViewHolder(@NonNull LocationItemViewHolder holder, int position) {
            Location location = trip.getLocations().get(position);

            holder.tripName.setText(location.getLocationName());
            holder.dateVisited.setText(sdf.format(location.getDateVisited()));

            holder.linearLayout.removeAllViews();
            if (!TextUtils.equals(location.getImageUrl(), "")) {
                ImageView imageView = new ImageView(getContext());
                imageView.setPadding(0, 10, 0, 0);
                Picasso.get().load(location.getImageUrl()).resize(700, 500).into(imageView);
                holder.linearLayout.addView(imageView);
            }
        }

        @Override
        public int getItemCount() {
            if (trip.getLocations() == null) {
                return 0;
            }
            return trip.getLocations().size();
        }
    }

    public class LocationItemViewHolder extends RecyclerView.ViewHolder {
        TextView tripName;
        TextView dateVisited;
        LinearLayout linearLayout;

        public LocationItemViewHolder(View itemView) {
            super(itemView);
            tripName = itemView.findViewById(R.id.location_name);
            dateVisited = itemView.findViewById(R.id.location_detail_date);
            linearLayout = itemView.findViewById(R.id.image_linear_layout);
        }
    }
}