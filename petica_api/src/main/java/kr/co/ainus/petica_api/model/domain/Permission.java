package kr.co.ainus.petica_api.model.domain;

import java.sql.Timestamp;;

import kr.co.ainus.petica_api.model.type.PermissionStateType;

public class Permission  {

    private long idx;

    private String masterEmail;

    private User slave;

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

    public User getSlave() {
        return slave;
    }

    public void setSlave(User slave) {
        this.slave = slave;
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
