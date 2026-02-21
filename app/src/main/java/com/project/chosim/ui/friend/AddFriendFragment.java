package com.project.chosim.ui.friend;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.project.chosim.databinding.FragmentAddFriendBinding;
import com.project.chosim.ui.BaseFragment;

public class AddFriendFragment extends BaseFragment {

    private FragmentAddFriendBinding binding;
    private AddFriendViewModel viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddFriendBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AddFriendViewModel.class);

        binding.emailTextField.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.addButton.setEnabled(Patterns.EMAIL_ADDRESS.matcher(s.toString().trim()).matches());
            }
        });

        binding.addButton.setOnClickListener(v -> search());

        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void search() {
        if (binding.progressView.getVisibility() == View.VISIBLE) return;

        String email = binding.emailTextField.getEditText().getText().toString().trim();
        if (TextUtils.equals(email, viewModel.getMyEmail())) {
            showToastMessage("자신의 이메일 주소는 입력하실 수 없습니다.");
            return;
        }

        binding.progressView.setVisibility(View.VISIBLE);

        viewModel.addFriend(email, result -> {
            if (result == null) {
                showToastMessage("오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.");
                binding.progressView.setVisibility(View.GONE);

            } else if (result) {
                showToastMessage("친구 추가가 완료되었습니다.");
                onBackPressed();

            } else {
                showToastMessage(email + "로 가입한 친구가 없습니다.");
                binding.progressView.setVisibility(View.GONE);
            }
        });
    }
}
