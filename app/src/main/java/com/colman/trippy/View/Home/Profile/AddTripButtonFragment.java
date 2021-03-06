package com.colman.trippy.View.Home.Profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.colman.trippy.R;

public class AddTripButtonFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentContainer =  inflater.inflate(R.layout.fragment_add_trip_button, container, false);
        fragmentContainer.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_userProfile_to_createTrip));

        return fragmentContainer;
    }
}