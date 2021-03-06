package com.colman.trippy.View.TripDetails;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.colman.trippy.Model.Trip;
import com.colman.trippy.Model.TripModel;
import com.colman.trippy.R;

import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

public class TripDetailsFragment extends Fragment {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trip_details, container, false);
        Trip trip = TripDetailsFragmentArgs.fromBundle(getArguments()).getTrip();

        TextView tripName = view.findViewById(R.id.trip_details_name);
        tripName.setText(trip.getName());

        TextView datesView = view.findViewById(R.id.trip_detail_dates);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String fromDate = sdf.format(trip.getFromDate());
        String untilDate = sdf.format(trip.getUntilDate());
        datesView.setText(String.format("%s - %s", fromDate, untilDate));

        TextView participantsView = view.findViewById(R.id.trip_detail_participants);
        if (trip.getParticipantsEmails().size() > 1) {
            participantsView.setText(trip.getParticipantsEmails().get(0).substring(0, 3).concat("... +" + (trip.getParticipantsEmails().size() - 1)));
        } else {
            participantsView.setText(trip.getParticipantsEmails().stream().collect(Collectors.joining(",")));
        }

        ImageView privateLockView = view.findViewById(R.id.trip_details_private_lock);
        if (trip.isTripPrivate()) {
            privateLockView.setVisibility(View.VISIBLE);
        }

        ImageView editButton = view.findViewById(R.id.trip_details_edit);

        ImageView deleteButton = view.findViewById(R.id.trip_details_delete);
        deleteButton.setOnClickListener(imageView -> TripModel.instance.removeTrip(trip, () -> {
            Log.d("TRIPLOG", "trip named " + trip.getName() + " deleted");
            Navigation.findNavController(deleteButton).popBackStack();
        }));

        if (trip.isCurrentUser()) {
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        }

        return view;
    }
}