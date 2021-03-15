package kr.co.ainus.petife2.view.dialog;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.DialogMediaSelectBinding;

public class MediaSelectDialog extends _BaseDialog {
    public MediaSelectDialog(@NonNull Context context) {
        super(context);

        DialogMediaSelectBinding dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_media_select, null, false);

        dataBinding.btnClose.setOnClickListener(v -> dismiss());

        setContentView(dataBinding.getRoot());
    }
}
