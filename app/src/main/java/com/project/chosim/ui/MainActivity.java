package com.project.chosim.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.project.chosim.R;
import com.project.chosim.databinding.ActivityMainBinding;
import com.project.chosim.ui.auth.SignInFragment;
import com.project.chosim.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    private ActivityResultLauncher<String> requestPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {

            });


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        if (viewModel.isSignedIn()) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new HomeFragment(), "Home")
                    .commit();

        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new SignInFragment(), "SignIn")
                    .commit();
        }

        viewModel.getAppUser().observe(this, appUser -> {
            if (appUser != null && getSupportFragmentManager().findFragmentByTag("Home") == null) {
                removeAllFragments(getSupportFragmentManager());

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment(), "Home")
                        .commit();

            } else if (appUser == null && getSupportFragmentManager().findFragmentByTag("SignIn") == null) {
                removeAllFragments(getSupportFragmentManager());

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SignInFragment(), "SignIn")
                        .commit();
            }
        });

        if (!checkPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermission.launch(Manifest.permission.SCHEDULE_EXACT_ALARM);
            }
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void removeAllFragments(FragmentManager fragmentManager) {
        while (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();
        }
    }
}
