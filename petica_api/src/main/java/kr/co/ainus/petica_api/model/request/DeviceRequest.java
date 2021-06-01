package kr.co.ainus.petica_api.model.request;

import java.sql.Timestamp;;

import kr.co.ainus.petica_api.model.type.FeedModeType;

public class DeviceRequest {
    private long idx;
    private String uuid;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public FeedModeType getFeedMode2() {
        return feedMode2;
    }

    public void setFeedMode2(FeedModeType feedMode2) {
        this.feedMode2 = feedMode2;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
