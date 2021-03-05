package com.colman.trippy.View.Home;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.colman.trippy.Model.UserModel;
import com.colman.trippy.R;
import com.colman.trippy.View.Login;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    NavController mNavController;
    private static final int REQUEST_WRITE_STORAGE = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserModel.instance.isLoggedIn(new UserModel.IsLoggedInListener() {
            @Override
            public void onComplete(Boolean result) {
                if (!result) {
                    Log.d("TRIPLOG", "User is not logged in, redirecting to login");
                    startActivity(new Intent(getApplicationContext(), Login.class));
                } else {
                    Log.d("TRIPLOG", "User is logged in, initiating tab layout");
                    initiateTabLayout();
                }
            }

            @Override
            public void onFailure(String message) {
                Log.d("TRIPLOG", "Couldn't decide whether user is connected or not because: " + message);
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        }
    }

    private void initiateTabLayout() {
        setContentView(R.layout.activity_main);
        TabLayout tabLayout = findViewById(R.id.app_tab_layout);
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("TRIPLOG", "Tab selected with position " + tab.getPosition());
                mNavController.navigate(getNavigationActionByPosition(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d("TRIPLOG", "Tab reselected with position " + tab.getPosition());
                mNavController.navigate(getNavigationActionByPosition(tab.getPosition()));
            }
        });
    }

    public int getNavigationActionByPosition(int position) {
        return position == 0 ? R.id.action_global_userProfileFragment : R.id.action_global_tripSearchFragment;
    }
}