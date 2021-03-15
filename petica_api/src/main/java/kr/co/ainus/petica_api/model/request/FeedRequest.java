package kr.co.ainus.petica_api.model.request;

import kr.co.ainus.petica_api.model.type.ExpireType;
import kr.co.ainus.petica_api.model.type.FeedModeType;
import kr.co.ainus.petica_api.model.type.FeedType;

public class FeedRequest {
    private long idx;
    private String uuid;
    private String deviceId;
    private FeedModeType mode;
    private FeedType type;
    private int hour;
    private int min;
    private int amount;
    private ExpireType expire;

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

    public FeedModeType getMode() {
        return mode;
    }

    public void setMode(FeedModeType mode) {
        this.mode = mode;
    }

    public FeedType getType() {
        return type;
    }

    public void setType(FeedType type) {
        this.type = type;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public ExpireType getExpire() {
        return expire;
    }

    public void setExpire(ExpireType expire) {
        this.expire = expire;
    }
}
