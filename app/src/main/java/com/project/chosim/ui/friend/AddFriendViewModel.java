package com.project.chosim.ui.friend;

import androidx.core.util.Consumer;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.chosim.data.repositories.AppUserRemoteRepository;

public class AddFriendViewModel extends ViewModel {

    private final AppUserRemoteRepository appUserRemoteRepository = AppUserRemoteRepository.getInstance();

    private final FirebaseAuth auth = FirebaseAuth.getInstance();


    public void addFriend(String email, Consumer<Boolean> callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        appUserRemoteRepository.addFriend(user.getUid(), email, callback);
    }

    public String getMyEmail() {
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;

        return user.getEmail();
    }
}
