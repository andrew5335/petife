package kr.co.ainus.peticaexcutor.callback;

import kr.co.ainus.peticaexcutor.type.StatusType;

public interface StatusCheckCallback {
    public void onCheck(StatusType statusType, boolean result);
}
