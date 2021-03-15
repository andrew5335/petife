package kr.co.ainus.petife2.view.dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.DialogPermissionRequestBinding;

public class PermissionRequestDialog extends _BaseDialog {

    private DialogPermissionRequestBinding dataBinding;

    private OnClickPermissionRequestListner onClickPermissionRequestListner;


    public PermissionRequestDialog(@NonNull Context context) {
        super(context);

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_permission_request, null, false);

        dataBinding.btnCancle.setOnClickListener(v -> {
            dismiss();
        });

        dataBinding.btnRequest.setOnClickListener(v -> {
            if (onClickPermissionRequestListner != null) onClickPermissionRequestListner.onClickPermissionRequest(PermissionRequestDialog.this, dataBinding.etEmail.getText().toString().trim());
        });

        setContentView(dataBinding.getRoot());
    }

    public void setOnClickPermissionRequestListner(OnClickPermissionRequestListner onClickPermissionRequestListner) {
        this.onClickPermissionRequestListner = onClickPermissionRequestListner;
    }

    public interface OnClickPermissionRequestListner {
        public void onClickPermissionRequest(Dialog dialog,  String email);
    }
}
