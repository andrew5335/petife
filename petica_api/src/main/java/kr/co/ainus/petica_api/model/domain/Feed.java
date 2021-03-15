package kr.co.ainus.petica_api.model.domain;

import android.util.Log;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;

import kr.co.ainus.petica_api.model.type.ExpireType;
import kr.co.ainus.petica_api.model.type.FeedModeType;
import kr.co.ainus.petica_api.model.type.FeedType;

public class Feed implements Comparable<Feed> {

    private long idx;
    private Petica petica;
    private FeedModeType mode;
    private FeedType type;
    private int hour;
    private int min;
    private int amount;
    private Timestamp next;
    private ExpireType expire;
    private Timestamp timestamp;

    public long getIdx() {
        return idx;
    }

    public void setIdx(long idx) {
        this.idx = idx;
    }

    public Petica getPetica() {
        return petica;
    }

    public void setPetica(Petica petica) {
        this.petica = petica;
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

    public Timestamp getNext() {
        return next;
    }

    public void setNext(Timestamp next) {
        this.next = next;
    }

    public ExpireType getExpire() {
        return expire;
    }

    public void setExpire(ExpireType expire) {
        this.expire = expire;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public int compareTo(Feed feed) {

        String srcTime = String.format("%02d%02d", this.hour, this.min);
        String dstTime = String.format("%02d%02d", feed.hour, feed.min);

        return srcTime.compareTo(dstTime);
    }
}
