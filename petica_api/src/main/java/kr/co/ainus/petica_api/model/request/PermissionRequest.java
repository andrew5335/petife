package kr.co.ainus.petica_api.model.request;

import java.sql.Timestamp;;

import kr.co.ainus.petica_api.model.type.PermissionStateType;

public class PermissionRequest {
    private long idx;
    private String masterEmail;
    private String uuid;
    private PermissionStateType state;
    private Timestamp timestamp;

    public long getIdx() {
        return idx;
    }

    public void setIdx(long idx) {
        this.idx = idx;
    }

    public String getMasterEmail() {
        return masterEmail;
    }

    public void setMasterEmail(String masterEmail) {
        this.masterEmail = masterEmail;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public PermissionStateType getState() {
        return state;
    }

    public void setState(PermissionStateType state) {
        this.state = state;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
