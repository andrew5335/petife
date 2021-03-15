package kr.co.ainus.petife2.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class _BaseViewModel extends ViewModel {
    private final MutableLiveData<Boolean> hasLoadingLiveData = new MutableLiveData<>();

    private final MutableLiveData<Integer> reasonCodeLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getHasLoadingLiveData() {
        return hasLoadingLiveData;
    }

    public MutableLiveData<Integer> getReasonCodeLiveData() {
        return reasonCodeLiveData;
    }
}

