package com.project.chosim.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public abstract class CombinedLiveData<A, B, R> extends MediatorLiveData<R> {
    private A a;
    private B b;

    public CombinedLiveData(LiveData<A> ld1, LiveData<B> ld2) {
        setValue(map(a, b));

        addSource(ld1, (a) -> {
            if (a != null) {
                this.a = a;
            }
            setValue(map(a, b));
        });

        addSource(ld2, (b) -> {
            if (b != null) {
                this.b = b;
            }
            setValue(map(a, b));
        });
    }

    abstract public R map(A a, B b);
}