package kr.co.ainus.petife2.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.DialogNumberPickerSingleBinding;
import kr.co.ainus.petica_api.model.type.FeedType;

public class NumberPickerSingleDialog extends _BaseDialog {

    private static final String TAG = "NumberPickerSingleDialog";
    private final FeedType feedType;
    private DialogNumberPickerSingleBinding dataBinding;
    private OnClickListener confirmOnClickListner;

    public NumberPickerSingleDialog(@NonNull Context context, FeedType feedType) {
        super(context);
        this.feedType = feedType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDataBinding();
        setViewModel();
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_number_picker_single, null, false);


        switch (feedType) {
            case FEEDER:
                dataBinding.np.setMinValue(3);
                dataBinding.np.setMaxValue(20);
                dataBinding.np.setValue(3);
                dataBinding.npValueText.setText("약" + dataBinding.np.getValue()*3 + "g");
                break;

            case WATER:
                dataBinding.np.setMinValue(3);
                dataBinding.np.setMaxValue(20);
                dataBinding.np.setValue(3);
                dataBinding.npValueText.setText("약" + dataBinding.np.getValue()*8 + "ml");
                break;

            default:
                dataBinding.np.setMinValue(3);
                dataBinding.np.setMaxValue(20);
                dataBinding.np.setValue(3);
                dataBinding.npValueText.setText("약" + dataBinding.np.getValue()*3 + "g");

        }

        dataBinding.np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Process the changes here
                if(feedType == FeedType.WATER) {
                    dataBinding.npValueText.setText("약" + dataBinding.np.getValue()*8 + "ml");
                } else if(feedType == FeedType.FEEDER) {
                    dataBinding.npValueText.setText("약" + dataBinding.np.getValue()*3 + "g");
                }
            }
        });
        dataBinding.btnCancel.setOnClickListener(v -> dismiss());
        dataBinding.btnConfirm.setOnClickListener(v -> {
            if (confirmOnClickListner != null) {
                dismiss();
                confirmOnClickListner.onClick(this, dataBinding.np.getValue());
            }
        });

        setContentView(dataBinding.getRoot());
    }

    @Override
    protected void onStart() {
    }

    @Override
    public void setViewModel() {
        super.setViewModel();
    }

    public void setOnConfirmClickListner(NumberPickerSingleDialog.OnClickListener onClickListener) {
        confirmOnClickListner = onClickListener;
    }

    public interface OnClickListener {
        void onClick(NumberPickerSingleDialog numberPickerSingleDialog, int selectValue);
    }
}
