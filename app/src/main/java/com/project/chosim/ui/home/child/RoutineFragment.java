package com.project.chosim.ui.home.child;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.project.chosim.Constants;
import com.project.chosim.data.model.Routine;
import com.project.chosim.databinding.FragmentRoutineBinding;
import com.project.chosim.ui.BaseFragment;
import com.project.chosim.ui.MainViewModel;
import com.project.chosim.ui.routine.RoutineEditorFragment;

import java.util.ArrayList;
import java.util.Date;

public class RoutineFragment extends BaseFragment {

    private final RoutineAdapter adapter = new RoutineAdapter();
    private FragmentRoutineBinding binding;
    private MainViewModel mainViewModel;
    private RoutineViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRoutineBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        viewModel = new ViewModelProvider(this, new RoutineViewModelFactory(mainViewModel.getAppUser()))
                .get(RoutineViewModel.class);

        // binding.toolbar.setNavigationOnClickListener(v -> startFragment(new SettingsFragment(), "Settings"));
        binding.dateTextView.setText(Constants.dateFormat.format(new Date()));
        binding.addButton.setOnClickListener(v -> startFragment(new RoutineEditorFragment(), "Editor"));

        binding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new RoutineAdapter.OnItemClickListener() {
            @Override
            public void onDoneCheckChanged(Routine routine, boolean isChecked) {
                viewModel.done(routine, binding.dateTextView.getText().toString().trim(), isChecked);
            }

            @Override
            public void onEditClicked(Routine routine) {
                startFragment(RoutineEditorFragment.getInstance(routine), "Editor");
            }
        });

        mainViewModel.getAppUser().observe(getViewLifecycleOwner(), appUser -> {
            if (appUser != null) {
                binding.pointTextView.setText("point: " + appUser.getPoint());
            }
        });

        viewModel.getRoutines().observe(getViewLifecycleOwner(), routines -> {
            if (routines == null) return;

            int doneCount = 0;
            for (Routine routine : routines) {
                if (routine.isMyDone()) {
                    doneCount += 1;
                }
            }

            binding.greateIndicator.setVisibility(View.GONE);
            binding.goodIndicator.setVisibility(View.GONE);
            binding.badIndicator.setVisibility(View.GONE);

            if (routines.size() > 0) {
                if (doneCount == routines.size()) {
                    binding.greateIndicator.setVisibility(View.VISIBLE);

                } else if (doneCount == 0) {
                    binding.badIndicator.setVisibility(View.VISIBLE);

                } else {
                    binding.goodIndicator.setVisibility(View.VISIBLE);
                }
            }

            adapter.submitList(routines);
        });
    }
}
