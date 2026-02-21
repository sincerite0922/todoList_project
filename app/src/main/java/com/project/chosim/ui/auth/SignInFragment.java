package com.project.chosim.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.project.chosim.R;
import com.project.chosim.databinding.FragmentSignInBinding;
import com.project.chosim.ui.BaseFragment;

public class SignInFragment extends BaseFragment {

    private final String TAG = "SignInFragment";

    private FragmentSignInBinding binding;
    private SignInViewModel viewModel;

    private GoogleSignInClient client;
    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != Activity.RESULT_OK) return;

                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                if (task.getException() != null) {
                    task.getException().printStackTrace();
                    return;
                }

                GoogleSignInAccount account = task.getResult();
                signIn(account.getIdToken());
            });


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SignInViewModel.class);

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        client = GoogleSignIn.getClient(requireActivity(), options);

        binding.googleButton.setOnClickListener(v -> googleSignInLauncher.launch(client.getSignInIntent()));
    }

    private void signIn(String idToken) {
        if (binding.progressView.getVisibility() == View.VISIBLE) return;

        binding.progressView.setVisibility(View.VISIBLE);

        viewModel.signIn(idToken, result -> {
            if (result == null) {
                binding.progressView.setVisibility(View.GONE);
                showToastMessage("로그인에 실패하였습니다. 잠시 후 다시 시도해 주세요.");
            }
        });
    }
}
