package kr.co.ainus.petife2.model.room;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import kr.co.ainus.petife2.BR;

@Entity
public class Post extends BaseObservable {

    @Bindable
    @PrimaryKey(autoGenerate = true)
    private long idx;

    @Bindable
    private long userIdx;

    @Bindable
    private String title;

    @Bindable
    private String message;

    @Bindable
    private String uri;

    @Bindable
    private int mediaType;

    @Bindable
    private int year;

    @Bindable
    private int month;

    @Bindable
    private int day;

    @Bindable
    private long timestamp;

    public long getIdx() {
        return idx;
    }

    public void setIdx(long idx) {
        this.idx = idx;
        notifyPropertyChanged(BR.idx);
    }

    public long getUserIdx() {
        return userIdx;
    }

    public void setUserIdx(long userIdx) {
        this.userIdx = userIdx;
        notifyPropertyChanged(BR.userIdx);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        notifyPropertyChanged(BR.message);
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
        notifyPropertyChanged(BR.uri);
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
        notifyPropertyChanged(BR.mediaType);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
        notifyPropertyChanged(BR.year);
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
        notifyPropertyChanged(BR.month);
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
        notifyPropertyChanged(BR.day);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        notifyPropertyChanged(BR.timestamp);
    }
}
