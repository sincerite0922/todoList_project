package com.project.chosim.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.project.chosim.R;
import com.project.chosim.databinding.FragmentHomeBinding;
import com.project.chosim.ui.BaseFragment;
import com.project.chosim.ui.home.child.CharacterFragment;
import com.project.chosim.ui.home.child.RoutineFragment;
import com.project.chosim.ui.home.child.EvaluationFragment;
import com.project.chosim.ui.home.child.FriendFragment;
import com.project.chosim.ui.home.child.SettingsFragment;

public class HomeFragment extends BaseFragment {

    private FragmentHomeBinding binding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.viewPager.setUserInputEnabled(false);
        binding.viewPager.setOffscreenPageLimit(4);
        binding.viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), getLifecycle()));

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.action_routine) {
                binding.viewPager.setCurrentItem(0);

            } else if (id == R.id.action_friends) {
                binding.viewPager.setCurrentItem(1);

            } else if (id == R.id.action_evaluation) {
                binding.viewPager.setCurrentItem(2);

            } else if (id == R.id.action_character) {
                binding.viewPager.setCurrentItem(3);

            } else {
                binding.viewPager.setCurrentItem(4);
            }

            return true;
        });
    }


    private static class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 1) {
                return new FriendFragment();

            } else if (position == 2) {
                return new EvaluationFragment();

            } else if (position == 3) {
                return new CharacterFragment();

            } else if (position == 4) {
                return new SettingsFragment();

            } else {
                return new RoutineFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 5;
        }
    }
}