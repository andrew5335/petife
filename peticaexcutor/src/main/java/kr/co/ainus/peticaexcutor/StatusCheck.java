package kr.co.ainus.peticaexcutor;

import androidx.annotation.NonNull;
import kr.co.ainus.peticaexcutor.callback.StatusCheckCallback;
import kr.co.ainus.peticaexcutor.type.StatusType;

public class StatusCheck {
    public static void check(byte status, @NonNull StatusCheckCallback statusCheckCallback) {
        for (StatusType value : StatusType.values()) {
            boolean result = (status & value.getValue()) == value.getValue();

            statusCheckCallback.onCheck(value, result);
        }
    }
}
