package kr.co.ainus.petife2.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.ActivityPetSettingBinding;
import kr.co.ainus.petife2.model.Pet;
import kr.co.ainus.petife2.util.GetRealPathHelper;
import kr.co.ainus.petife2.util.MediaHelper;

public class PetSettingActivity extends _BaseNavigationActivity {

    private static final String TAG = "PetSettingActivity";

    private ActivityPetSettingBinding dataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDataBinding();
        setViewModel();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                // TODO 음성녹음에 대한 권한 필요 안내 후 액티비티 종료

                finish();
            }

        }
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        baseNavigationBinding.tvTitle.setText(getString(R.string.petSetting));
        baseNavigationBinding.btnRight.setText(getString(R.string.store));
        baseNavigationBinding.btnRight.setOnClickListener(v -> {
            savePet(dataBinding.getPet());
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.petInfoSave))
                    .setPositiveButton(getString(R.string.confirm), (dailog,which) -> {
                        finish();
                    })
                    .create();

            alertDialog.show();
        });

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_pet_setting, baseNavigationBinding.baseNavContainer, true);
        dataBinding.setLifecycleOwner(this);


        dataBinding.btnPhotoAdd.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setItems(new String[]{
                            getString(R.string.getGalleryPhoto)
//                            , "카메라 촬영"
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    MediaHelper.getPhotoFromGallery(PetSettingActivity.this);
                                    break;

                                case 1:
                                    MediaHelper.takePhoto(PetSettingActivity.this);
                                    break;
                            }
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create().show();
        });

        dataBinding.tvBirthday.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(this
                    , android.R.style.Theme_Holo_Light_Dialog_MinWidth
                    , (view, year, monthOfYear, dayOfMonth) -> {

//                petViewModel.setBirth(year, monthOfYear, dayOfMonth);
                Pet pet = dataBinding.getPet();
                pet.setBirthYear(year);
                pet.setBirthMonth(monthOfYear + 1);
                pet.setBirthDay(dayOfMonth);
            }
                    , c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.getDatePicker().setCalendarViewShown(false);
            datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            datePickerDialog.show();


        });


    }

    @Override
    public void setViewModel() {
        super.setViewModel();

        petViewModel.loadPet(getApplicationContext());

        petViewModel.getPetLiveData().observe(this, pet -> dataBinding.setPet(pet));
        petViewModel.getPetLiveData().observe(this, pet -> {
            if (pet == null) {
                pet = new Pet();
            }

            dataBinding.setPet(pet);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            MediaHelper.clearCache();
            return;
        }

        if (requestCode == MediaHelper.PICK_PHOTO_FROM_GALLERY) {

            new Thread(() -> {

                try {

                    File srcFile = new File(GetRealPathHelper.getRealPath(getApplicationContext(), data.getData()));
                    //File dstFile = new File(PeticaCameraActivity.DATA_PATH + srcFile.getName() + System.currentTimeMillis());
                    File dstFile = new File(getApplicationContext().getFilesDir() + srcFile.getName() + System.currentTimeMillis());

                    if (!dstFile.getParentFile().exists()) {
                        dstFile.getParentFile().mkdirs();
                    }

                    FileInputStream fileInputStream = new FileInputStream(srcFile);
                    FileOutputStream fileOutputStream= new FileOutputStream(dstFile);
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

                    int readData;
                    while((readData=bufferedInputStream.read())!=-1) {
                        bufferedOutputStream.write(readData);
                    }

                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();

                    bufferedInputStream.close();

                    fileOutputStream.flush();
                    fileOutputStream.close();

                    fileInputStream.close();

                    String uriStrinig = Uri.fromFile(dstFile).toString();

                    if (dataBinding.getPet() != null) {
                        dataBinding.getPet().setPhotoUri(uriStrinig);
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    e.printStackTrace();
                }

            }).start();


        }
        /** 2019-04-21
         * 카메라 촬영 파일 처리
         * 주석처리 */
        /*else if (requestCode == PICK_PHOTO_FROM_CAMERA) {
            photoUri = MediaHelper.getTakePhotoUri();
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
        }*/



        /*String photoRealPath = GetRealPathHelper.getRealPath(getApplicationContext(), photoUri);

        if (dataBinding.getPet() != null) {
            dataBinding.getPet().setPhotoUri(photoRealPath);
        }

        Log.i(TAG, "photoUri = " + photoUri);
        Log.i(TAG, "photoRealPath = " + photoRealPath);*/
    }

    private void savePet(Pet pet) {
        pet.setName(dataBinding.etName.getText().toString());

        String petGender = null;
        if (dataBinding.rbGenderMale.isChecked()) {
            petGender = "m";
        }

        if (dataBinding.rbGenderFemale.isChecked()) {
            petGender = "f";
        }

        pet.setGender(petGender);

        pet.setKind(dataBinding.etKind.getText().toString());
        pet.setWeight(dataBinding.etWeight.getText().toString());

        petViewModel.savePet(getApplicationContext(), pet);
    }


}
