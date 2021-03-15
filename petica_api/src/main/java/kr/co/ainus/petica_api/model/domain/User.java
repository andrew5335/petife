package kr.co.ainus.petica_api.model.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class User {
    @Expose
    @SerializedName("timestamp")
    private Timestamp timestamp;
    @Expose
    @SerializedName("uuid")
    private String uuid;
    @Expose
    @SerializedName("provider")
    private String provider;
    @Expose
    @SerializedName("email")
    private String email;
    @Expose
    @SerializedName("idx")
    private int idx;

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }
}
