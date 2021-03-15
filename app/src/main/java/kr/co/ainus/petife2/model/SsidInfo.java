package kr.co.ainus.petife2.model;

import com.google.gson.annotations.SerializedName;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import kr.co.ainus.petife2.BR;

public class SsidInfo extends BaseObservable implements Comparable<SsidInfo> {

    @Bindable
    @SerializedName("ssid")
    private String ssid;

    @Bindable
    @SerializedName("bssid")
    private String bssid;

    @Bindable
    private String password;

    @Bindable
    @SerializedName("channel")
    private int channel;

    @Bindable
    @SerializedName("level")
    private int level;

    @Bindable
    private String flags;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
        notifyPropertyChanged(BR.ssid);
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
        notifyPropertyChanged(BR.bssid);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
        notifyPropertyChanged(BR.channel);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        notifyPropertyChanged(BR.level);
    }

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
        notifyPropertyChanged(BR.flags);
    }

    @Override
    public int compareTo(SsidInfo ssidInfo) {

        if (this.level > ssidInfo.level) {
            return 11;
        } else if (this.level == ssidInfo.level) {
            return 0;
        } else {
            return 1;
        }
    }
}
