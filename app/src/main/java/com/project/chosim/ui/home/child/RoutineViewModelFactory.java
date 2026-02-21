package com.project.chosim.ui.home.child;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.project.chosim.data.model.AppUser;
import com.project.chosim.ui.home.child.RoutineViewModel;

public class RoutineViewModelFactory implements ViewModelProvider.Factory {

    private final LiveData<AppUser> liveData;

    public RoutineViewModelFactory(LiveData<AppUser> liveData) {
        this.liveData = liveData;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new RoutineViewModel(liveData);
    }
}
