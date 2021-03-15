package kr.co.ainus.petife2.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import kr.co.ainus.petife2.R;
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

public class _BaseDialog extends Dialog implements Extandable {
    private static final String TAG = "_BaseDialog";

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

    public _BaseDialog(@NonNull Context context) {
        super(context);

        email = SharedPreferencesHelper.getString(context, "user_email");
        provider = SharedPreferencesHelper.getString(context, "user_provider");
        uuid = SharedPreferencesHelper.getString(context, "user_uuid");
        hasFirst = SharedPreferencesHelper.getBoolean(context, "user_has_first");

        Log.i(TAG, "user_email =" + email);
        Log.i(TAG, "user_provider =" + provider);
        Log.i(TAG, "user_uuid =" + uuid);
        Log.i(TAG, "user_has_first =" + hasFirst);

        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (this.getWindow().getAttributes() != null) {
            WindowManager.LayoutParams params = this.getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            this.getWindow().getDecorView().setBackgroundResource(R.color.transparent);
            this.getWindow().setAttributes(params);
        }
    }

    @Override
    public void setDataBinding() {

    }

    @Override
    public void setViewModel() {
        if (getOwnerActivity() instanceof AppCompatActivity) {
            baseViewModel = ViewModelProviders.of((FragmentActivity) getOwnerActivity()).get(_BaseViewModel.class);
            userViewModel = ViewModelProviders.of((FragmentActivity) getOwnerActivity()).get(UserViewModel.class);
            petViewModel = ViewModelProviders.of((FragmentActivity) getOwnerActivity()).get(PetViewModel.class);
            peticaViewModel = ViewModelProviders.of((FragmentActivity) getOwnerActivity()).get(PeticaViewModel.class);
            postViewModel = ViewModelProviders.of((FragmentActivity) getOwnerActivity()).get(PostViewModel.class);
            feedViewModel = ViewModelProviders.of((FragmentActivity) getOwnerActivity()).get(FeedViewModel.class);
            feedHistoryViewModel = ViewModelProviders.of((FragmentActivity) getOwnerActivity()).get(FeedHistoryViewModel.class);
            permissionViewModel = ViewModelProviders.of((FragmentActivity) getOwnerActivity()).get(PermissionViewModel.class);
        }
    }
}
