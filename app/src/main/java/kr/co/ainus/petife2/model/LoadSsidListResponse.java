package kr.co.ainus.petife2.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoadSsidListResponse {

    @SerializedName("wifimessage")
    private List<SsidInfo> wifimessage;

    public List<SsidInfo> getWifimessage() {
        return wifimessage;
    }

    public void setWifimessage(List<SsidInfo> wifimessage) {
        this.wifimessage = wifimessage;
    }
}

