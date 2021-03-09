package com.colman.trippy.Firebase;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.colman.trippy.AppConsts;
import com.colman.trippy.Model.Trip;
import com.colman.trippy.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public class TripFirebaseModel {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fireStore = FirebaseFirestore.getInstance();

    public void getAllTrips(Long dataVersion, final AppConsts.Listener<ArrayList<Trip>> listener) {
        fireStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Trip> trips = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        boolean isOwner = (document.toObject(User.class).getEmail().equals(firebaseAuth.getCurrentUser().getEmail()));
                        List<Trip> userTrips = document.toObject(User.class).getTrips().stream()
                                .filter(trip -> (isOwner || trip.getParticipantsEmails().contains(firebaseAuth.getCurrentUser().getEmail())))
                                .collect(Collectors.toList());
                        userTrips.forEach(trip -> {
                            trip.setCurrentUser(true);
                            trip.setOwnerUser(document.toObject(User.class).getEmail());
                        });
                        trips.addAll(userTrips);
                        Log.d("TRIPLOG", "(Firebase)" + document.getId() + " => " + document.getData());
                    }
                    listener.onComplete(trips);
                } else {
                    Log.d("TRIPLOG", "(Firebase) Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void addTrip(Trip trip, final AppConsts.Listener<Boolean> listener) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference currentUserRef = fireStore.collection("users").document(userId);
        currentUserRef.update("trips", FieldValue.arrayUnion(trip.toMap())).addOnSuccessListener((result) -> {
            Log.d("TRIPLOG", "(Firebase) Adding trips for user " + userId + " done with success");
            listener.onComplete(true);
        }).addOnFailureListener((Exception result) -> {
            Log.d("TRIPLOG", "(Firebase) Adding trips for user " + userId + " failed. Reason: " + result.getMessage());
            listener.onComplete(false);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getTrip(String name, final AppConsts.Listener<Trip> listener) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        fireStore.collection("users").document(userId).get().addOnCompleteListener(task -> {
            Trip trip = null;
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc != null) {
                    DocumentSnapshot result = task.getResult();
                    User user = result.toObject(User.class);
                    trip = user.getTrips().stream().filter(t -> t.getName().equals(name)).findAny().orElse(null);
                }
            }
            listener.onComplete(trip);
        });
    }

    public void removeTrip(Trip trip, final AppConsts.Listener<Boolean> listener) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference currentUserRef = fireStore.collection("users").document(userId);
        currentUserRef.update("trips", FieldValue.arrayRemove(trip.toMap())).addOnSuccessListener((result) -> {
            Log.d("TRIPLOG", "(Firebase) Deleting trips of user " + userId + " done with success");
            listener.onComplete(true);
        }).addOnFailureListener((Exception result) -> {
            Log.d("TRIPLOG", "(Firebase) Deleting trips of user " + userId + " failed. Reason: " + result.getMessage());
            listener.onComplete(false);
        });
    }

    public void uploadImage(Bitmap imageBmp, String name, final AppConsts.Listener<String> listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference imagesRef = storage.getReference().child("images").child(name);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                Log.d("TRIPLOG", "Failed uploading image " + name + " reason: " + exception.getMessage());
                listener.onFailure(exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;
                        listener.onComplete(downloadUrl.toString());
                    }
                });
            }
        });
    }

    public void getSearchedTrips(final AppConsts.Listener<ArrayList<Trip>> listener, String query) {
        fireStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Trip> trips = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        boolean isFilterPrivate = !(document.toObject(User.class).getEmail().equals(firebaseAuth.getCurrentUser().getEmail()));
                        List<Trip> userTrips = document.toObject(User.class).getTrips().stream()
                                .filter(trip -> !(isFilterPrivate && trip.isTripPrivate()))
                                .filter(trip -> trip.getName().toLowerCase().contains(query.toLowerCase()))
                                .collect(Collectors.toList());
                        userTrips.forEach(trip -> {
                            trip.setOwnerUser(document.toObject(User.class).getEmail());
                            if (trip.getParticipantsEmails().contains(firebaseAuth.getCurrentUser().getEmail())) {
                                trip.setCurrentUser(true);
                            }
                        });
                        if (!isFilterPrivate) {
                            userTrips.forEach(trip -> trip.setCurrentUser(true));
                        }
                        trips.addAll(userTrips);
                        Log.d("TRIPLOG", "(Firebase)" + document.getId() + " => " + document.getData());
                    }
                    listener.onComplete(trips);
                } else {
                    Log.d("TRIPLOG", "(Firebase) Error getting documents: ", task.getException());
                }
            }
        });
    }
}
