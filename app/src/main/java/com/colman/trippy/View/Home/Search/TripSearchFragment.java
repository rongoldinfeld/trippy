package com.colman.trippy.View.Home.Search;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.colman.trippy.AppConsts;
import com.colman.trippy.Model.Location;
import com.colman.trippy.Model.SearchModel;
import com.colman.trippy.Model.Trip;
import com.colman.trippy.R;
import com.colman.trippy.Trippy;
import com.colman.trippy.ViewModel.SearchViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.colman.trippy.AppConsts.sdf;

public class TripSearchFragment extends Fragment {
    SearchView searchView;
    RecyclerView recyclerView;
    SearchViewModel searchViewModel;
    SearchTripListAdapter adapter;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View searchViewFragment = inflater.inflate(R.layout.fragment_trip_search, container, false);

        searchView = searchViewFragment.findViewById(R.id.search_input);
        searchView.setIconifiedByDefault(false);

        progressBar = searchViewFragment.findViewById(R.id.search_list_progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        recyclerView = searchViewFragment.findViewById(R.id.search_list);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SearchTripListAdapter();
        recyclerView.setAdapter(adapter);
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        searchViewModel.getTripList().observe(getViewLifecycleOwner(), trips -> {
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchValue) {
                refreshSearchData(searchValue);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String searchValue) {
                refreshSearchData(searchValue);
                return true;
            }
        });

        refreshSearchData("");

        return searchViewFragment;
    }

    private  void refreshSearchData(String searchValue) {
        progressBar.setVisibility(View.VISIBLE);
        SearchModel.instance.searchTrips(searchValue);
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
            Trip trip = searchViewModel.getTripList().getValue().get(position);

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
            }

            holder.linearLayout.removeAllViews();
            for (Location loc : trip.getLocations()) {
                ImageView imageView = new ImageView(getContext());
                imageView.setPadding(10, 0, 10, 0);
                Picasso.get().load(loc.getImageUrl()).resize(100, 100).into(imageView);
                holder.linearLayout.addView(imageView);
            }
        }

        @Override
        public int getItemCount() {
            if (searchViewModel.getTripList() == null) {
                return 0;
            }
            return searchViewModel.getTripList().getValue().size();
        }
    }

    public class TripItemViewHolder extends RecyclerView.ViewHolder {
        TextView tripName;
        TextView dates;
        TextView participants;
        ImageView isPrivateLock;
        LinearLayout linearLayout;

        public TripItemViewHolder(View itemView) {
            super(itemView);
            tripName = itemView.findViewById(R.id.trip_name);
            dates = itemView.findViewById(R.id.dates_text);
            participants = itemView.findViewById(R.id.participants_text);
            isPrivateLock = itemView.findViewById(R.id.is_private_lock);
            linearLayout = itemView.findViewById(R.id.image_linear_layout);
        }
    }
}