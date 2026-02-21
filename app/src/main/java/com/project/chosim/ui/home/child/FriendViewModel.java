package com.project.chosim.ui.home.child;

import android.text.TextUtils;

import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.project.chosim.Constants;
import com.project.chosim.data.model.Done;
import com.project.chosim.data.model.Routine;
import com.project.chosim.data.repositories.AppUserRemoteRepository;
import com.project.chosim.data.repositories.RoutineRemoteRepository;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class FriendViewModel extends ViewModel {

    private final AppUserRemoteRepository appUserRemoteRepository = AppUserRemoteRepository.getInstance();
    private final RoutineRemoteRepository routineRemoteRepository = RoutineRemoteRepository.getInstance();

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<String> selectedFriendName = new MutableLiveData<>();
    private final MutableLiveData<String> selectedFriendUid = new MutableLiveData<>();
    private final LiveData<List<Routine>> routines;

    private ListenerRegistration routineListenerRegistration = null;
    private ListenerRegistration doneListenerRegistration = null;


    public FriendViewModel() {
        Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        String date = Constants.dateFormat.format(calendar.getTime());

        this.routines = Transformations.switchMap(selectedFriendUid, friendUid -> {
            removeListenerRegistration();

            if (TextUtils.isEmpty(friendUid)) {
                return new MutableLiveData<>(Collections.emptyList());
            }

            final MutableLiveData<List<Routine>> _routines = new MutableLiveData<>();
            final MutableLiveData<List<Done>> _doneList = new MutableLiveData<>();

            routineListenerRegistration = routineRemoteRepository.getRoutines(friendUid, week, _routines::setValue);
            doneListenerRegistration = routineRemoteRepository.getDoneList(friendUid, date, _doneList::setValue);

            return routineRemoteRepository.combine(_routines, _doneList);
        });
    }

    public void selectFriend(String uid, String userName) {
        this.selectedFriendUid.setValue(uid);
        this.selectedFriendName.setValue(userName);
    }

    public void clearFriendSelection() {
        selectFriend(null, null);
    }

    public String getSelectedFriendUidValue() {
        return selectedFriendUid.getValue() != null ? selectedFriendUid.getValue() : null;
    }

    public LiveData<List<Routine>> getRoutines() {
        return routines;
    }

    public LiveData<String> getFriendName() {
        return selectedFriendName;
    }

    public void removeFriend(String uid, Consumer<Boolean> callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        appUserRemoteRepository.removeFriend(user.getUid(), uid, callback);
    }

    private void removeListenerRegistration() {
        if (routineListenerRegistration != null) {
            routineListenerRegistration.remove();
            routineListenerRegistration = null;
        }

        if (doneListenerRegistration != null) {
            doneListenerRegistration.remove();
            doneListenerRegistration = null;
        }
    }

    @Override
    protected void onCleared() {
        removeListenerRegistration();

        super.onCleared();
    }
}
