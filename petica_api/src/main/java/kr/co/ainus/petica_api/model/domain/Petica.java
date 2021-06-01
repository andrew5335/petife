package kr.co.ainus.petica_api.model.domain;

import java.sql.Timestamp;;

import kr.co.ainus.petica_api.model.type.FeedModeType;

public class Petica {

    private long idx;
    private User user;
    private String deviceId;
    private String deviceName;
    private String devicePw;
    private FeedModeType feedMode;
    private FeedModeType feedMode2;
    private Timestamp timestamp;

    public long getIdx() {
        return idx;
    }

    public void setIdx(long idx) {
        this.idx = idx;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDevicePw() {
        return devicePw;
    }

    public void setDevicePw(String devicePw) {
        this.devicePw = devicePw;
    }

    public FeedModeType getFeedMode() {
        return feedMode;
    }

    public void setFeedMode(FeedModeType feedMode) {
        this.feedMode = feedMode;
    }

    public FeedModeType getFeedMode2() { return feedMode2; }

    public void setFeedMode2(FeedModeType feedMode2) { this.feedMode2 = feedMode2; }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
