package kr.co.ainus.petife2.view.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.ActivityPostAddBinding;
import kr.co.ainus.petife2.model.room.Post;
import kr.co.ainus.petife2.util.GetRealPathHelper;
import kr.co.ainus.petife2.util.MediaHelper;

public class PostAddActivity extends _BaseNavigationActivity {

    private static final String TAG = "PostAddActivity";

    public static String mode = "";
    public static Post post;

    private ActivityPostAddBinding dataBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDataBinding();
        setViewModel();
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

        baseNavigationBinding.tvTitle.setText(getString(R.string.petDiary) + " " + PostAddActivity.mode);
        baseNavigationBinding.btnRight.setOnClickListener(v -> {
            Post post = dataBinding.getPost();

            String title = dataBinding.etTitle.getText().toString();
            String message = dataBinding.etMessage.getText().toString();

            post.setTitle(title);
            post.setMessage(message);

            DateTime dateTime = new DateTime(DateTimeZone.forOffsetHours(9)).withDate(post.getYear(), post.getMonth(), post.getDay());
            post.setTimestamp(dateTime.getMillis());

            String write = getString(R.string.write);
            String modify = getString(R.string.modify);

            if(PostAddActivity.mode.equals(write)) {
                postViewModel.postAdd(getApplicationContext(), post);
            } else if(PostAddActivity.mode.equals(modify)) {
                postViewModel.postUpdate(getApplicationContext(), post);
            }

            /**
            switch (PostAddActivity.mode) {
                case "작성":
                    postViewModel.postAdd(getApplicationContext(), post);

                    break;

                case "수정":
                    postViewModel.postUpdate(getApplicationContext(), post);

                    break;
            }
             **/

            finish();
        });

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_post_add, baseNavigationBinding.baseNavContainer, true);
        dataBinding.setLifecycleOwner(this);

        if (PostAddActivity.post == null) {
            PostAddActivity.post = new Post();

            DateTime dateTime = new DateTime(DateTimeZone.forOffsetHours(9));
            PostAddActivity.post.setYear(getIntent().getIntExtra("year", dateTime.getYear()));
            PostAddActivity.post.setMonth(getIntent().getIntExtra("month", dateTime.getMonthOfYear()));
            PostAddActivity.post.setDay(getIntent().getIntExtra("day", dateTime.getDayOfMonth()));
        }
        dataBinding.setPost(PostAddActivity.post);

        dataBinding.btnPhotoOrVideoAdd.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.choice))
                    .setItems(new String[]{
                            getString(R.string.getGalleryPhoto)
                            , getString(R.string.getGalleryVideo)
//                            , "앱에서 사진 가져오기"
//                            , "앱에서 동영상 가져오기"
                    }, ((dialog, which) -> {

                        switch (which) {

                            case 0:
                                MediaHelper.getPhotoFromGallery(PostAddActivity.this);

                                break;

                            case 1:
                                MediaHelper.getVideoFromGallery(PostAddActivity.this);

                                break;

                            case 3:
                                MediaHelper.getPhotoFromApp(PostAddActivity.this);

                                break;

                            case 4:
                                MediaHelper.getVideoFromApp(PostAddActivity.this);

                                break;
                        }

                    }))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create();

            try {

                alertDialog.show();

            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                e.printStackTrace();
            }

        });

        dataBinding.btnDate.setOnClickListener(v -> {

            DatePickerDialog datePickerDialog = new DatePickerDialog(this
                    , android.R.style.Theme_Holo_Light_Dialog_MinWidth
                    , (view, year, monthOfYear, dayOfMonth) -> {

                dataBinding.getPost().setYear(year);
                dataBinding.getPost().setMonth(monthOfYear + 1);
                dataBinding.getPost().setDay(dayOfMonth);

            }
                    , dataBinding.getPost().getYear(), dataBinding.getPost().getMonth() - 1, dataBinding.getPost().getDay());

            datePickerDialog.getDatePicker().setCalendarViewShown(false);

            try {

                datePickerDialog.show();

            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                e.printStackTrace();
            }

        });
    }

    @Override
    public void setViewModel() {
        super.setViewModel();

        postViewModel.getSelectDateTimeLiveData().observe(this, dateTime -> {

            Log.d(TAG, dateTime.getYear() + ", " + dateTime.getMonthOfYear() + ", " + dateTime.getDayOfMonth());

            PostAddActivity.post.setYear(dateTime.getYear());
            PostAddActivity.post.setMonth(dateTime.getMonthOfYear());
            PostAddActivity.post.setDay(dateTime.getDayOfMonth());

            PostAddActivity.post.setTimestamp(dateTime.getMillis());

            dataBinding.setPost(PostAddActivity.post);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {


            new Thread(() -> {

                try {

                    File srcFile = new File(GetRealPathHelper.getRealPath(getApplicationContext(), data.getData()));
                    //File dstFile = new File(PeticaCameraActivity.DATA_PATH + srcFile.getName() + System.currentTimeMillis());
                    // 파일 저장 위치 수정 2020-07-27 by Andrew Kim
                    File dstFile = new File(getApplicationContext().getFilesDir() + srcFile.getName() + System.currentTimeMillis());
                    //File dstFile = new File("/storage/Android/data/kr.co.ainus.petica/PETICA/" + srcFile.getName() + System.currentTimeMillis());
                    //File dstFile = new File(PeticaCameraActivity.DATA_PATH + srcFile.getName());

                    if (dstFile.getParentFile().exists()) {
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

                    switch (requestCode) {
                        case MediaHelper.PICK_PHOTO_FROM_GALLERY:

                            dataBinding.getPost().setUri(uriStrinig);
                            dataBinding.getPost().setMediaType(MediaHelper.PHOTO);
                            break;

                        case MediaHelper.PICK_VIDEO_FROM_GALLERY:
                            dataBinding.getPost().setUri(uriStrinig);
                            dataBinding.getPost().setMediaType(MediaHelper.VIDEO);

                            break;

                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    e.printStackTrace();
                }

            }).start();

        }
    }
}
