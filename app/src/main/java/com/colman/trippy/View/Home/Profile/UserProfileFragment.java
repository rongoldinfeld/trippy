package com.colman.trippy.View.Home.Profile;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.colman.trippy.AppConsts;
import com.colman.trippy.Model.Trip;
import com.colman.trippy.Model.TripModel;
import com.colman.trippy.Model.UserModel;
import com.colman.trippy.R;
import com.colman.trippy.View.Login;
import com.colman.trippy.ViewModel.TripViewModel;

import java.util.List;
import java.util.stream.Collectors;

import static com.colman.trippy.AppConsts.sdf;

public class UserProfileFragment extends Fragment {
    TripViewModel viewModel;
    ImageView logoutButton;

    ProgressBar pb;
    SwipeRefreshLayout sref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        logoutButton = view.findViewById(R.id.logout_image);
        viewModel = new ViewModelProvider(this).get(TripViewModel.class);
        RecyclerView rv = view.findViewById(R.id.user_trips_list);
        UserTripListAdapter adapter = new UserTripListAdapter();

        pb = view.findViewById(R.id.trip_list_progress_bar);
        pb.setVisibility(View.INVISIBLE);
        sref = view.findViewById(R.id.swiperefresh);

        sref.setOnRefreshListener(() -> {
            sref.setRefreshing(true);
            reloadData(adapter);
        });

        rv.setAdapter(adapter);
        rv.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                Log.d("TRIPLOG", "Click on item list" + e.getActionIndex());
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        viewModel.getTripList().observe(getViewLifecycleOwner(), new Observer<List<Trip>>() {
            @Override
            public void onChanged(List<Trip> trips) {
                adapter.notifyDataSetChanged();
            }
        });

        logoutButton.setOnClickListener(imageView -> {
            UserModel.instance.logout();
            startActivity(new Intent(getContext(), Login.class));
        });

        return view;
    }

    private void reloadData(UserTripListAdapter adapter) {
        pb.setVisibility(View.VISIBLE);
        TripModel.instance.refreshTrips(() -> {
            pb.setVisibility(View.INVISIBLE);
            sref.setRefreshing(false);
        });
    }


    class UserTripListAdapter extends RecyclerView.Adapter<UserTripListAdapter.TripItemViewHolder> {

        @NonNull
        @Override
        public TripItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_trip_list_row, parent, false);
            return new TripItemViewHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onBindViewHolder(@NonNull TripItemViewHolder holder, int position) {
            Trip t = viewModel.getTripList().getValue().get(position);
            String fromDate = sdf.format(t.getFromDate());
            String untilDate = sdf.format(t.getUntilDate());
            String participants = t.getParticipantsEmails().stream().collect(Collectors.joining(","));

            holder.tripName.setText(t.getName());
            holder.dates.setText(fromDate + " - " + untilDate);
            holder.participants.setText(participants);
        }

        @Override
        public int getItemCount() {
            if (viewModel.getTripList().getValue() == null) {
                return 0;
            }
            return viewModel.getTripList().getValue().size();
        }

        public class TripItemViewHolder extends RecyclerView.ViewHolder {
            TextView tripName;
            TextView dates;
            TextView participants;

            public TripItemViewHolder(View itemView) {
                super(itemView);
                tripName = itemView.findViewById(R.id.trip_name);
                dates = itemView.findViewById(R.id.dates_text);
                participants = itemView.findViewById(R.id.participants_text);
            }
        }
    }


}