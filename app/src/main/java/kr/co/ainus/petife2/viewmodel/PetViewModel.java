package kr.co.ainus.petife2.viewmodel;

import android.content.Context;
import android.util.Log;

import java.util.Calendar;
import java.util.Locale;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import kr.co.ainus.petife2.model.Pet;
import kr.co.ainus.petife2.util.SharedPreferencesHelper;

public class PetViewModel extends ViewModel {
    private static final String TAG = "PetViewModel";

    private final MutableLiveData<Pet> petLiveData = new MutableLiveData<>();

    public MutableLiveData<Pet> getPetLiveData() {
        return petLiveData;
    }

    public void loadPet(Context context) {

        String petName = SharedPreferencesHelper.getString(context, "pet_name");
        int petBirthYear = SharedPreferencesHelper.getInt(context, "pet_birth_year");
        int petBirthMonth = SharedPreferencesHelper.getInt(context, "pet_birth_month");
        int petBirthDay = SharedPreferencesHelper.getInt(context, "pet_birth_day");
        String petGender = SharedPreferencesHelper.getString(context, "pet_gender");
        String petKind = SharedPreferencesHelper.getString(context, "pet_kind");
        String petWeight = SharedPreferencesHelper.getString(context, "pet_weight");
        String petPhotoUri = SharedPreferencesHelper.getString(context, "pet_photo_uri");

        if (petName == null || petName.isEmpty()) {
            String language = "";
            language = Locale.getDefault().getLanguage().toString();

            if(null != language && !"".equals(language)) {
                if("en".equals(language)) {
                    petName = "Set up your pet";
                } else if("ja".equals(language)) {
                    petName = "ペットを設定します";
                } else {
                    petName = "펫을 설정하세요";
                }
            } else {
                petName = "펫을 설정하세요";
            }
        }

        if(petBirthYear == 0 || petBirthMonth == 0 ||petBirthDay == 0) {

            Calendar calendar = Calendar.getInstance(Locale.KOREA);
            petBirthYear = calendar.get(Calendar.YEAR);
            petBirthMonth = calendar.get(Calendar.MONTH) + 1;
            petBirthDay = calendar.get(Calendar.DAY_OF_MONTH);
        }

        if (petGender == null || petGender.isEmpty()) {
            petGender = "f";
        }

        if (petKind == null || petKind.isEmpty()) {
            petKind = "Petife";
        }

        if (petWeight == null || petWeight.isEmpty()) {
            petWeight = "2.2";
        }

        Pet pet = new Pet(petName, petBirthYear, petBirthMonth, petBirthDay, petGender, petKind, petWeight, petPhotoUri);

        Log.i(TAG, "pet name = " + petName);
        Log.i(TAG, "pet birth year = " + petBirthYear);
        Log.i(TAG, "pet birth month = " + petBirthMonth);
        Log.i(TAG, "pet birth day = " + petBirthDay);
        Log.i(TAG, "pet gender = " + petGender);
        Log.i(TAG, "pet kind = " + petKind);
        Log.i(TAG, "pet weight = " + petWeight);
        Log.i(TAG, "pet photo uri = " + petPhotoUri);

        petLiveData.setValue(pet);

    }

    public void savePet(Context context, Pet pet) {

        final String PET_NAME = pet.getName();
        final int PET_BIRTH_YEAR = pet.getBirthYear();
        final int PET_BIRTH_MONTH = pet.getBirthMonth();
        final int PET_BIRTH_DAY = pet.getBirthDay();
        final String PET_GENDER = pet.getGender();
        final String PET_KIND = pet.getKind();
        final String PET_WEIGHT = pet.getWeight();
        final String PET_PHOTO_URI = pet.getPhotoUri();

        SharedPreferencesHelper.putString(context, "pet_name", PET_NAME);
        SharedPreferencesHelper.putInt(context, "pet_birth_year", PET_BIRTH_YEAR);
        SharedPreferencesHelper.putInt(context, "pet_birth_month", PET_BIRTH_MONTH);
        SharedPreferencesHelper.putInt(context, "pet_birth_day", PET_BIRTH_DAY);
        SharedPreferencesHelper.putString(context, "pet_gender", PET_GENDER);
        SharedPreferencesHelper.putString(context, "pet_kind", PET_KIND);
        SharedPreferencesHelper.putString(context, "pet_weight", PET_WEIGHT);
        SharedPreferencesHelper.putString(context, "pet_photo_uri", PET_PHOTO_URI);
    }

    public void setBirth(int year, int monthOfYear, int dayOfMonth) {
        try {

            Log.i(TAG, "year = " + year);
            Log.i(TAG, "monthOfYear = " + monthOfYear);
            Log.i(TAG, "dayOfMonth = " + dayOfMonth);

            if (petLiveData.getValue() != null) {
                petLiveData.getValue().setBirthYear(year);
                petLiveData.getValue().setBirthMonth(monthOfYear + 1);
                petLiveData.getValue().setBirthDay(dayOfMonth);
            }


        } catch (Exception e) {

            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();

        }
    }
}
