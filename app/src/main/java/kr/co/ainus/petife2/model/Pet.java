package kr.co.ainus.petife2.model;

import java.util.Calendar;
import java.util.Date;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import kr.co.ainus.petife2.BR;

public class Pet extends BaseObservable {
    @Bindable
    private String name;

    @Bindable
    private int birthYear;

    @Bindable
    private int birthMonth;

    @Bindable
    private int birthDay;

    @Bindable
    private String gender;

    @Bindable
    private String kind;

    @Bindable
    private String weight;

    @Bindable
    private String photoUri;

    @Bindable
    private int age;

    public Pet(String name, int birthYear, int birthMonth, int birthDay, String gender, String kind, String weight, String photoUri) {
        this.name = name;
        this.birthYear = birthYear;
        this.birthMonth = birthMonth;
        this.birthDay = birthDay;
        this.gender = gender;
        this.kind = kind;
        this.weight = weight;
        this.photoUri = photoUri;
    }

    public Pet() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
        notifyPropertyChanged(BR.birthYear);
    }

    public int getBirthMonth() {
        return birthMonth;
    }

    public void setBirthMonth(int birthMonth) {
        this.birthMonth = birthMonth;
        notifyPropertyChanged(BR.birthMonth);
    }

    public int getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(int birthDay) {
        this.birthDay = birthDay;
        notifyPropertyChanged(BR.birthDay);
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
        notifyPropertyChanged(BR.gender);
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
        notifyPropertyChanged(BR.kind);
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
        notifyPropertyChanged(BR.weight);
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
        notifyPropertyChanged(BR.photoUri);
    }

    public int getAge() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(birthYear, birthMonth, birthDay);

        long birthdayTime = calendar.getTimeInMillis();
        long nowTime = new Date().getTime();

        age = (int) ((nowTime - birthdayTime) / (1000 * 60 * 60 * 24) / 365);

        return age;
    }

    public void setAge(int age) {
        this.age = age;

        notifyPropertyChanged(BR.age);
    }
}
