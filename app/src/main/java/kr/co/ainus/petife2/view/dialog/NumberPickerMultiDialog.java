package kr.co.ainus.petife2.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.NumberPicker;

import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import kr.co.ainus.petica_api.model.type.FeedModeType;
import kr.co.ainus.petica_api.model.type.FeedType;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.DialogNumberPickerMultiBinding;

public class NumberPickerMultiDialog extends _BaseDialog {

    private static final String TAG = "NumberPickerSingleDialog";
    private final String peticaId;
    private DialogNumberPickerMultiBinding dataBinding;
    private OnClickListener confirmOnClickListner;
    private FeedType feedType;

    public NumberPickerMultiDialog(@NonNull Context context, String peticaId, FeedModeType feedModeType, FeedType feedType) {
        super(context);
        this.peticaId = peticaId;
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

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_number_picker_multi, null, false);

        Calendar calendar = Calendar.getInstance(Locale.KOREA);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        dataBinding.npHour.setMinValue(0);
        dataBinding.npHour.setMaxValue(23);
        dataBinding.npHour.setValue(hour);

        dataBinding.npMinute.setMinValue(0);
        dataBinding.npMinute.setMaxValue(59);
        dataBinding.npMinute.setValue(minute);

        dataBinding.npAmount.setMinValue(2);
        dataBinding.npAmount.setMaxValue(20);
        dataBinding.npAmount.setValue(2);

        if(feedType == FeedType.WATER) {
            dataBinding.npAmountText.setText("약" + dataBinding.npAmount.getValue()*8 + "ml");
        } else if(feedType == FeedType.FEEDER) {
            dataBinding.npAmountText.setText("약" + dataBinding.npAmount.getValue()*3 + "g");
        }

        dataBinding.btnCancel.setOnClickListener(v -> dismiss());
        dataBinding.btnConfirm.setOnClickListener(v -> {
            if (confirmOnClickListner != null) {
                dismiss();
                confirmOnClickListner.onClick(this, dataBinding.npHour.getValue(), dataBinding.npMinute.getValue(), dataBinding.npAmount.getValue());
            }
        });

        dataBinding.npAmount.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Process the changes here
                if(feedType == FeedType.WATER) {
                    dataBinding.npAmountText.setText("약" + dataBinding.npAmount.getValue()*8 + "ml");
                } else if(feedType == FeedType.FEEDER) {
                    dataBinding.npAmountText.setText("약" + dataBinding.npAmount.getValue()*3 + "g");
                }
            }
        });

        setContentView(dataBinding.getRoot());
    }

    @Override
    public void setViewModel() {
        super.setViewModel();
    }

    public void setOnConfirmClickListner(NumberPickerMultiDialog.OnClickListener onClickListener) {
        confirmOnClickListner = onClickListener;
    }

    public interface OnClickListener {
        void onClick(NumberPickerMultiDialog numberPickerlDialog, int hour, int minute, int amount);
    }
}
