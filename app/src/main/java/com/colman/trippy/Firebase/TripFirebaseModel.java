package com.colman.trippy.Firebase;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.colman.trippy.AppConsts;
import com.colman.trippy.Model.Trip;
import com.colman.trippy.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TripFirebaseModel {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fireStore = FirebaseFirestore.getInstance();

    public void getAllTrips(Long dataVersion, final AppConsts.Listener<ArrayList<Trip>> listener) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        fireStore.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ArrayList<Trip> data = new ArrayList<Trip>();

                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();
                    User user = result.toObject(User.class);
                    data = user.getTrips();
                    data.stream().filter(trip -> trip.getDataVersion() > dataVersion);
                }

                listener.onComplete(data);
            }
        });
    }

    public void addTrip(Trip trip, final AppConsts.Listener<Boolean> listener) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference currentUserRef = fireStore.collection("users").document(userId);
        currentUserRef.update("trips", FieldValue.arrayUnion(trip.toMap())).addOnSuccessListener((result) -> {
            Log.d("TRIPLOG", "(Firebase) Updating trips of user " + userId + " done with success");
            listener.onComplete(true);
        }).addOnFailureListener((Exception result) -> {
            Log.d("TRIPLOG", "(Firebase) Updating trips of user " + userId + " failed. Reason: " + result.getMessage());
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

    //TODO: Implement delete trip

    //TODO: Implement update trip

    //TODO: Implement uploadImage
//    public void uploadImage(Bitmap imageBmp, String name, final Model.UploadImageListener listener){
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        final StorageReference imagesRef = storage.getReference().child("images").child(name);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] data = baos.toByteArray();
//        UploadTask uploadTask = imagesRef.putBytes(data);
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(Exception exception) {
//                listener.onComplete(null);
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        Uri downloadUrl = uri;
//                        listener.onComplete(downloadUrl.toString());
//                    }
//                });
//            }
//        });
//    }
}
