package com.project.chosim.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;
import com.project.chosim.data.model.AppUser;
import com.project.chosim.data.repositories.AppUserRemoteRepository;
import com.project.chosim.data.repositories.RoutineRemoteRepository;

import java.util.Calendar;

public class MainViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final AppUserRemoteRepository appUserRemoteRepository = AppUserRemoteRepository.getInstance();
    private final RoutineRemoteRepository routineRemoteRepository = RoutineRemoteRepository.getInstance();

    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private final MutableLiveData<AppUser> appUser = new MutableLiveData<>();
    private ListenerRegistration listenerRegistration;


    public MainViewModel() {
        auth.addAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            removeListenerRegistration();
            appUser.setValue(null);

        } else {
            if (listenerRegistration == null) {
                listenerRegistration = appUserRemoteRepository.getAppUser(user.getUid(), this.appUser::setValue);
            }

            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DATE, -1);
            routineRemoteRepository.rateRoutines(user.getUid(), yesterday);
        }
    }

    public LiveData<AppUser> getAppUser() {
        return appUser;
    }

    public boolean isSignedIn() {
        return auth.getCurrentUser() != null;
    }

    private void removeListenerRegistration() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }

    @Override
    protected void onCleared() {
        removeListenerRegistration();
        super.onCleared();
    }
}
