package com.colman.trippy.View.Home.Search;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.colman.trippy.AppConsts;
import com.colman.trippy.Firebase.TripFirebaseModel;
import com.colman.trippy.Model.Trip;
import com.colman.trippy.Model.TripModel;
import com.colman.trippy.R;
import com.colman.trippy.Trippy;
import com.colman.trippy.View.Home.Profile.UserProfileFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.colman.trippy.AppConsts.sdf;

public class TripSearchFragment extends Fragment {
    SearchView searchView;
    RecyclerView recyclerView;
    List<Trip> trips;
    TripFirebaseModel tripFirebaseModel = new TripFirebaseModel();
    SearchTripListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View searchViewFragment = inflater.inflate(R.layout.fragment_trip_search, container, false);

        searchView = searchViewFragment.findViewById(R.id.search_input);
        searchView.setIconifiedByDefault(false);

        recyclerView = searchViewFragment.findViewById(R.id.search_list);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SearchTripListAdapter();
        recyclerView.setAdapter(adapter);

        final SharedPreferences sp = Trippy.context.getSharedPreferences("TAG", Context.MODE_PRIVATE);
        long dataVersion = sp.getLong("dataVersion", 0);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return getSearchedTripsByQuery(s, dataVersion, sp);
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return getSearchedTripsByQuery(s, dataVersion, sp);
            }
        });

        return searchViewFragment;
    }

    private boolean getSearchedTripsByQuery(String s, long dataVersion, SharedPreferences sp) {
        tripFirebaseModel.getSearchedTrips(dataVersion, new AppConsts.Listener<ArrayList<Trip>>() {
            @Override
            public void onComplete(ArrayList<Trip> result) {
                long lastDataVersion = 0;
                trips = result;
                adapter.notifyDataSetChanged();
                sp.edit().putLong("dataVersion", lastDataVersion).apply();
            }

            @Override
            public void onFailure(String message) {
                Log.d("TRIPLOG", "Failed to retrieve all trips. Error" + message);
            }
        }, s);
        return true;
    }

    class SearchTripListAdapter extends RecyclerView.Adapter<TripItemViewHolder> {
        @NonNull
        @Override
        public TripItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_trip_list_row, parent, false);
            return new TripItemViewHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onBindViewHolder(@NonNull TripItemViewHolder holder, int position) {
            Trip trip = trips.get(position);

            String fromDate = sdf.format(trip.getFromDate());
            String untilDate = sdf.format(trip.getUntilDate());
            String participants;
            if (trip.getParticipantsEmails().size() > 1) {
                participants = trip.getParticipantsEmails().get(0).substring(0, 3).concat("... +" + (trip.getParticipantsEmails().size() - 1));
            } else {
                participants = trip.getParticipantsEmails().stream().collect(Collectors.joining(","));
            }

            holder.tripName.setText(trip.getName());
            holder.dates.setText(fromDate + " - " + untilDate);
            holder.participants.setText(participants);
            if (trip.isTripPrivate()) {
                holder.isPrivateLock.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            if (trips == null) {
                return 0;
            }
            return trips.size();
        }
    }

    public class TripItemViewHolder extends RecyclerView.ViewHolder {
        TextView tripName;
        TextView dates;
        TextView participants;
        ImageView isPrivateLock;

        public TripItemViewHolder(View itemView) {
            super(itemView);
            tripName = itemView.findViewById(R.id.trip_name);
            dates = itemView.findViewById(R.id.dates_text);
            participants = itemView.findViewById(R.id.participants_text);
            isPrivateLock = itemView.findViewById(R.id.is_private_lock);
        }
    }
}