package com.project.chosim.ui.routine;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;
import com.project.chosim.data.model.Routine;
import com.project.chosim.data.repositories.RoutineRemoteRepository;

import java.util.Map;

public class RoutineEditorViewModel extends ViewModel {

    private final RoutineRemoteRepository routineRemoteRepository = RoutineRemoteRepository.getInstance();

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private ListenerRegistration listenerRegistration;

    private final MutableLiveData<Routine> routine = new MutableLiveData<>();


    public RoutineEditorViewModel(Routine routine) {
        if (routine != null) {
            this.routine.setValue(routine);

            listenerRegistration = routineRemoteRepository.getRoutine(routine.getDocumentId(), result -> {
                if (result == null) {
                    this.routine.setValue(null);
                }
            });
        }
    }

    public void create(String title,
                       boolean alarm,
                       String alarmTime,
                       Map<String, Boolean> cycle,
                       boolean repeat,
                       boolean privateRoutine,
                       Pair<String, String> friend) {
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;

        routineRemoteRepository.createRoutine(user.getUid(), user.getDisplayName(),
                title, alarm, alarmTime, cycle, repeat, privateRoutine, friend);
    }

    public void update(String title,
                       boolean alarm,
                       String alarmTime,
                       Map<String, Boolean> cycle,
                       boolean repeat,
                       boolean privateRoutine) {
        Routine routine = this.routine.getValue();
        assert routine != null;

        routineRemoteRepository.updateRoutine(routine, title, alarm, alarmTime, cycle, repeat, privateRoutine);
    }

    public void delete() {
        Routine routine = this.routine.getValue();
        assert routine != null;

        routineRemoteRepository.deleteRoutine(routine);
    }

    public LiveData<Routine> getRoutine() {
        return routine;
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }
}
