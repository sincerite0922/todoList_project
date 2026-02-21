package com.project.chosim.ui.home.child;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;
import com.project.chosim.data.repositories.AppUserRemoteRepository;

import java.time.YearMonth;
import java.util.Map;

public class EvaluationViewModel extends ViewModel {

    private AppUserRemoteRepository appUserRemoteRepository = AppUserRemoteRepository.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private MutableLiveData<YearMonth> selectedMonth = new MutableLiveData<>();
    public LiveData<Map<Integer, Integer>> rates = Transformations.switchMap(selectedMonth, yearMonth -> {
        if (yearMonth == null) {
            return new MutableLiveData<>(null);
        }

        removeListenerRegistration();

        FirebaseUser user = auth.getCurrentUser();
        assert user != null;

        final MutableLiveData<Map<Integer, Integer>> _rates = new MutableLiveData<>();

        listenerRegistration = appUserRemoteRepository.getRates(
                user.getUid(), yearMonth.getYear(), yearMonth.getMonthValue(), _rates::setValue);

        return _rates;
    });

    private ListenerRegistration listenerRegistration = null;


    public LiveData<Map<Integer, Integer>> getRates() {
        return rates;
    }

    public void selectMonth(YearMonth yearMonth) {
        selectedMonth.setValue(yearMonth);
    }

    @Override
    protected void onCleared() {
        removeListenerRegistration();
        super.onCleared();
    }

    private void removeListenerRegistration() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }
}
