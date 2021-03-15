package kr.co.ainus.petife2.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.FragmentBaseBinding;
import kr.co.ainus.petife2.protocol.Extandable;
import kr.co.ainus.petife2.util.SharedPreferencesHelper;
import kr.co.ainus.petife2.viewmodel.FeedHistoryViewModel;
import kr.co.ainus.petife2.viewmodel.FeedViewModel;
import kr.co.ainus.petife2.viewmodel.PermissionViewModel;
import kr.co.ainus.petife2.viewmodel.PetViewModel;
import kr.co.ainus.petife2.viewmodel.PeticaViewModel;
import kr.co.ainus.petife2.viewmodel.PostViewModel;
import kr.co.ainus.petife2.viewmodel.UserViewModel;
import kr.co.ainus.petife2.viewmodel._BaseViewModel;

abstract public class _BaseFragment extends Fragment implements Extandable {

    private static final String TAG = "_BaseFragment";

    protected FragmentBaseBinding baseDataBinding;

    protected _BaseViewModel baseViewModel;
    protected UserViewModel userViewModel;
    protected PeticaViewModel peticaViewModel;
    protected PostViewModel postViewModel;
    protected FeedViewModel feedViewModel;
    protected FeedHistoryViewModel feedHistoryViewModel;
    protected PermissionViewModel permissionViewModel;
    protected PetViewModel petViewModel;

    protected String email;
    protected String provider;
    protected String uuid;
    protected boolean hasFirst;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() != null) {
            email = SharedPreferencesHelper.getString(getActivity(), "user_email");
            provider = SharedPreferencesHelper.getString(getActivity(), "user_provider");
            uuid = SharedPreferencesHelper.getString(getActivity(), "user_uuid");
            hasFirst = SharedPreferencesHelper.getBoolean(getActivity(), "user_has_first");

            Log.i(TAG, "user_email =" + email);
            Log.i(TAG, "user_provider =" + provider);
            Log.i(TAG, "user_uuid =" + uuid);
            Log.i(TAG, "user_has_first =" + hasFirst);

            if (uuid == null) {
                uuid = UUID.randomUUID().toString();
                SharedPreferencesHelper.putString(getContext(), "user_uuid", uuid);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // System.gc() // 가비지 역시 하는거 아니었어;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return baseDataBinding.getRoot();
    }

    @Override
    public void setDataBinding() {
        baseDataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout._fragment_base, null, false);
        baseDataBinding.setLifecycleOwner(this);
    }

    @Override
    public void setViewModel() {
        if (getActivity() != null) {
            baseViewModel = ViewModelProviders.of(getActivity()).get(_BaseViewModel.class);
            userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
            petViewModel = ViewModelProviders.of(getActivity()).get(PetViewModel.class);
            peticaViewModel = ViewModelProviders.of(getActivity()).get(PeticaViewModel.class);
            postViewModel = ViewModelProviders.of(getActivity()).get(PostViewModel.class);
            feedViewModel = ViewModelProviders.of(getActivity()).get(FeedViewModel.class);
            feedHistoryViewModel = ViewModelProviders.of(getActivity()).get(FeedHistoryViewModel.class);
            permissionViewModel = ViewModelProviders.of(getActivity()).get(PermissionViewModel.class);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

}
