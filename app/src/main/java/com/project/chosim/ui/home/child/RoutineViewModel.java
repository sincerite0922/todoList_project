package com.project.chosim.ui.home.child;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.project.chosim.Constants;
import com.project.chosim.data.model.AppUser;
import com.project.chosim.data.model.Done;
import com.project.chosim.data.model.Routine;
import com.project.chosim.data.repositories.RoutineRemoteRepository;

import java.util.Calendar;
import java.util.List;

public class RoutineViewModel extends ViewModel {

    private final RoutineRemoteRepository routineRemoteRepository = RoutineRemoteRepository.getInstance();

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final MutableLiveData<List<Routine>> _routines = new MutableLiveData<>();
    private final MutableLiveData<List<Done>> _doneList = new MutableLiveData<>();
    private final LiveData<List<Routine>> routines;

    private ListenerRegistration routineListenerRegistration = null;
    private ListenerRegistration doneListenerRegistration = null;

    public RoutineViewModel(LiveData<AppUser> appUserLiveData) {
        fetchData();

        routines = routineRemoteRepository.combine(_routines, _doneList);
    }

    public void fetchData() {
        removeListenerRegistration();

        FirebaseUser user = auth.getCurrentUser();
        assert user != null;

        Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        String date = Constants.dateFormat.format(calendar.getTime());

        routineListenerRegistration = routineRemoteRepository.getRoutines(user.getUid(), week, _routines::setValue);
        doneListenerRegistration = routineRemoteRepository.getDoneList(user.getUid(), date, _doneList::setValue);
    }

    public LiveData<List<Routine>> getRoutines() {
        return routines;
    }

    public void done(Routine routine, String date, boolean selected) {
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;

        List<Routine> routines = _routines.getValue();
        assert routines != null;

        routineRemoteRepository.done(user.getUid(), routine, date, selected);
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
        super.onCleared();
        removeListenerRegistration();
    }
}
