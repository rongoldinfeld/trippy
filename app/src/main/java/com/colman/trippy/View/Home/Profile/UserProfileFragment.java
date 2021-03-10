package com.colman.trippy.View.Home.Profile;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.colman.trippy.AppConsts;
import com.colman.trippy.Model.Location;
import com.colman.trippy.Model.Trip;
import com.colman.trippy.Model.TripModel;
import com.colman.trippy.Model.User;
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
    TextView userGreeting;
    View userProfileView;
    ProgressBar pb;
    SwipeRefreshLayout sref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        userProfileView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        logoutButton = userProfileView.findViewById(R.id.logout_image);
        userGreeting = userProfileView.findViewById(R.id.user_greeting);
        viewModel = new ViewModelProvider(this).get(TripViewModel.class);
        RecyclerView rv = userProfileView.findViewById(R.id.user_trips_list);
        UserTripListAdapter adapter = new UserTripListAdapter();

        pb = userProfileView.findViewById(R.id.trip_list_progress_bar);
        pb.setVisibility(View.INVISIBLE);
        sref = userProfileView.findViewById(R.id.swiperefresh);

        sref.setOnRefreshListener(() -> {
            sref.setRefreshing(true);
            reloadData();
        });

        rv.setAdapter(adapter);
        rv.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                Log.d("TRIPLOG", "Touch on item list" + e.getActionIndex());
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                Log.d("TRIPLOG", "onRequestDisallowInterceptTouchEvent");

            }
        });

        viewModel.getTripList().observe(getViewLifecycleOwner(), new Observer<List<Trip>>() {
            @Override
            public void onChanged(List<Trip> trips) {
                adapter.notifyDataSetChanged();
            }
        });

        logoutButton.setOnClickListener(imageView -> {
            UserModel.instance.logout(() -> {
                startActivity(new Intent(getContext(), Login.class));
            });
        });

        UserModel.instance.getCurrentUser(new AppConsts.Listener<User>() {
            @Override
            public void onComplete(User result) {
                userGreeting.setText("Hey there " + result.getFullName());
                adapter.setCurrentUser(result);
            }

            @Override
            public void onFailure(String message) {
                Log.d("TRIPLOG", "Failed to get user profile, couldn't show greetings to user");
            }
        });

        reloadData();
        return userProfileView;
    }

    private void reloadData() {
        pb.setVisibility(View.VISIBLE);
        TripModel.instance.refreshTrips(() -> {
            pb.setVisibility(View.INVISIBLE);
            sref.setRefreshing(false);
        });
    }


    class UserTripListAdapter extends RecyclerView.Adapter<UserTripListAdapter.TripItemViewHolder> {

        private User currentUser;

        @NonNull
        @Override
        public TripItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_trip_list_row, parent, false);
            return new TripItemViewHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onBindViewHolder(@NonNull TripItemViewHolder holder, int position) {
            Trip trip = viewModel.getTripList().getValue().get(position);
            String fromDate = sdf.format(trip.getFromDate());
            String untilDate = sdf.format(trip.getUntilDate());
            String participants;
            if (trip.getParticipantsEmails().size() > 1) {
                participants = trip.getParticipantsEmails().get(0).substring(0, 3).concat("... +" + (trip.getParticipantsEmails().size() - 1));
            } else {
                participants = trip.getParticipantsEmails().stream().collect(Collectors.joining(","));
            }

            holder.tripName.setText(trip.getName());
            holder.dates.setText(String.format("%s - %s", fromDate, untilDate));
            holder.participants.setText(participants);
            if (trip.isTripPrivate()) {
                holder.isPrivateLock.setVisibility(View.VISIBLE);
            } else {
                holder.isPrivateLock.setVisibility(View.INVISIBLE);
            }

            if (this.currentUser != null && TextUtils.equals(trip.getOwnerUser(), this.currentUser.getEmail())) {
                holder.deleteButton.setVisibility(View.VISIBLE);
            } else {
                holder.deleteButton.setVisibility(View.INVISIBLE);
            }

            holder.linearLayout.removeAllViews();
            for (Location loc : trip.getLocations()) {
                if (!TextUtils.equals(loc.getImageUrl(), "")) {
                    ImageView imageView = new ImageView(getContext());
                    imageView.setPadding(10, 0, 10, 0);
                    AppConsts.loadPicture(loc.getImageUrl(), 100, 100, imageView);
                    holder.linearLayout.addView(imageView);
                }
            }

            holder.deleteButton.setOnClickListener(imageView -> TripModel.instance.removeTrip(trip, () -> Log.d("TRIPLOG", "trip named " + trip.getName() + " deleted")));

            holder.itemView.setOnClickListener(view -> {
                UserProfileFragmentDirections.ActionUserProfileToTripDetails action = UserProfileFragmentDirections.actionUserProfileToTripDetails(trip);
                Navigation.findNavController(userProfileView).navigate(action);
            });
            holder.itemView.setClickable(true);
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
            ImageView isPrivateLock;
            LinearLayout linearLayout;
            ImageView deleteButton;

            public TripItemViewHolder(View itemView) {
                super(itemView);
                tripName = itemView.findViewById(R.id.trip_name);
                dates = itemView.findViewById(R.id.dates_text);
                participants = itemView.findViewById(R.id.participants_text);
                isPrivateLock = itemView.findViewById(R.id.is_private_lock);
                linearLayout = itemView.findViewById(R.id.image_linear_layout);
                deleteButton = itemView.findViewById(R.id.delete_button);
            }
        }

        public void setCurrentUser(User currentUser) {
            this.currentUser = currentUser;
        }
    }
}