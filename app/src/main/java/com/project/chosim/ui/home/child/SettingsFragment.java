package com.project.chosim.ui.home.child;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.project.chosim.R;
import com.project.chosim.databinding.FragmentSettingsBinding;
import com.project.chosim.ui.BaseFragment;

public class SettingsFragment extends BaseFragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        binding.nameTextField.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.doneButton.setEnabled(!s.toString().trim().isEmpty());
            }
        });

        binding.nameTextField.getEditText().setText(viewModel.getDisplayName());

        binding.doneButton.setOnClickListener(v -> changeDisplayName());

        // binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_sign_out) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setMessage("로그아웃 하시겠습니까?")
                        .setPositiveButton("확인", (dialog, which) -> {
                            signOut();
                        })
                        .setNegativeButton("취소", null)
                        .show();
            }

            return true;
        });
    }

    private void changeDisplayName() {
        if (binding.progressView.getVisibility() == View.VISIBLE) return;

        binding.progressView.setVisibility(View.VISIBLE);

        String name = binding.nameTextField.getEditText().getText().toString().trim();
        viewModel.changeDisplayName(name, result -> {
            if (result) {
                showToastMessage("이름이 변경되었습니다.");
                // onBackPressed();

            } else {
                showToastMessage("이름이 변경에 실패하였습니다. 잠시 후 다시 시도해 주세요.");
                binding.progressView.setVisibility(View.GONE);
            }
        });
    }

    private void signOut() {
        viewModel.signOut();
        showToastMessage("로그아웃 되었습니다.");
    }
}
