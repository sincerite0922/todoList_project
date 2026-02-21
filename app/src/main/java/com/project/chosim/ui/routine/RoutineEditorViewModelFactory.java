package com.project.chosim.ui.routine;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.project.chosim.data.model.Routine;

public class RoutineEditorViewModelFactory implements ViewModelProvider.Factory {

    private final Routine routine;

    public RoutineEditorViewModelFactory(Routine routine) {
        this.routine = routine;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new RoutineEditorViewModel(routine);
    }
}
