package kr.co.ainus.petife2.view.dialog;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.DialogWifiPasswordBinding;
import kr.co.ainus.petife2.model.SsidInfo;

public class WifiPasswordDialog extends _BaseDialog {

    private static final String TAG = "WifiPasswordDialog";

    private DialogWifiPasswordBinding dataBinding;

    public WifiPasswordDialog(@NonNull Context context, SsidInfo ssidInfo) {
        super(context);

        setDataBinding();
        setViewModel();

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_wifi_password, null, false);
        dataBinding.setSsidInfo(ssidInfo);
        dataBinding.tgbShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Log.e(TAG, "isChecked = " + isChecked);
                dataBinding.setHasShowPassword(isChecked);
                Log.e(TAG, "input mode = " + dataBinding.etPassword.getInputType());
            }
        });

        dataBinding.executePendingBindings();

        setContentView(dataBinding.getRoot());
    }

    public void setOnClickCancelButton(View.OnClickListener onClickListener) {
        dataBinding.btnClose.setOnClickListener(onClickListener);
    }

    public void setOnClickConfirmButton(View.OnClickListener onClickListener) {
        dataBinding.btnConfirm.setOnClickListener(onClickListener);
    }

}
