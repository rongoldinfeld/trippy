package com.colman.trippy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.colman.trippy.models.UserModel;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    NavController mNavController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
//        this.requestWindowFeature(Window.FEATURE);
//        actionBar
        //Remove notification bar
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_black_24dp);
//        getActionBar().setHomeButtonEnabled(true);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_main_toolbar);
        setSupportActionBar(myToolbar);

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