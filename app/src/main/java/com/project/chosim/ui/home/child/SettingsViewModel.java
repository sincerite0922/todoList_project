package com.project.chosim.ui.home.child;

import androidx.core.util.Consumer;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.chosim.data.repositories.AppUserRemoteRepository;

public class SettingsViewModel extends ViewModel {

    private final AppUserRemoteRepository appUserRemoteRepository = AppUserRemoteRepository.getInstance();

    private final FirebaseAuth auth = FirebaseAuth.getInstance();


    public String getDisplayName() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return "";

        return user.getDisplayName();
    }

    public void changeDisplayName(String displayName, Consumer<Boolean> callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        appUserRemoteRepository.changeDisplayName(user, displayName, callback);
    }

    public void signOut() {
        auth.signOut();
    }
}
