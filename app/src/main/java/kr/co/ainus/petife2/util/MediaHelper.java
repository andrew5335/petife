package kr.co.ainus.petife2.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import kr.co.ainus.petife2.view.activity.PeticaCameraActivity;

public class MediaHelper {
    public static final int PICK_PHOTO_FROM_CAMERA = 0x00;

    public static final int PICK_PHOTO_FROM_GALLERY = 0x01;
    public static final int PICK_VIDEO_FROM_GALLERY = 0x02;
    public static final int PHOTO = 0x01;
    public static final int VIDEO = 0x02;

    private static File cacheFile;
    private static Uri photoUri;

    public static void getPhotoFromGallery(Activity activity) {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        activity.startActivityForResult(intent, PICK_PHOTO_FROM_GALLERY);
    }

    public static void getVideoFromGallery(Activity activity) {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Video.Media.CONTENT_TYPE);
        activity.startActivityForResult(intent, PICK_VIDEO_FROM_GALLERY);
    }

    public static void takePhoto(Activity activity) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(takePictureIntent, PICK_PHOTO_FROM_CAMERA);
        }
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        try {
//            cacheFile = createImageFile();
//        } catch (IOException e) {
//            Toast.makeText(activity, "이미지 처리 오류! 다시 시도하세요.", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//
//        if (cacheFile != null) {
//
//            photoUri = Uri.fromFile(cacheFile);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//            activity.startActivityForResult(intent, PICK_PHOTO_FROM_CAMERA);
//        }
    }

    public static Uri getTakePhotoUri() {
        return photoUri;
    }

    public static void clearCache() {
        if (cacheFile != null) {
            cacheFile.deleteOnExit();
        }
    }

    private static File createImageFile() throws IOException {

        // 이미지 파일 이름 ( blackJin_{시간}_ )
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "petica" + timeStamp + "_";

        // 이미지가 저장될 폴더 이름 ( blackJin )
        File storageDir = new File(PeticaCameraActivity.DATA_PATH);
        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }

    public static void getPhotoFromApp(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(PeticaCameraActivity.DATA_PATH);
        intent.setDataAndType(uri, "image/**");
        activity.startActivity(Intent.createChooser(intent, "Open folder"));
    }

    public static void getVideoFromApp(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(PeticaCameraActivity.DATA_PATH);
        intent.setDataAndType(uri, "video/**");
        activity.startActivity(Intent.createChooser(intent, "Open folder"));
    }
}
