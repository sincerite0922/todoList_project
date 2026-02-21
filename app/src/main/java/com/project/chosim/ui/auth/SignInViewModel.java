package com.project.chosim.ui.auth;

import androidx.core.util.Consumer;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.project.chosim.data.repositories.AppUserRemoteRepository;

public class SignInViewModel extends ViewModel {

    private final AppUserRemoteRepository appUserRemoteRepository = AppUserRemoteRepository.getInstance();


    public void signIn(String idToken, Consumer<FirebaseUser> callback) {
        appUserRemoteRepository.signIn(idToken, callback);
    }
}
