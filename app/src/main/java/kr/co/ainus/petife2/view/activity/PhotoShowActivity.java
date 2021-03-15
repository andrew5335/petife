package kr.co.ainus.petife2.view.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.ActivityPhotoShowBinding;
import kr.co.ainus.petife2.databinding.ActivityVideoPlayBinding;

public class PhotoShowActivity extends AppCompatActivity {

    public static String uri;

    private ActivityPhotoShowBinding dataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        if (getActionBar() != null) getActionBar().hide();

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_photo_show);
        dataBinding.setLifecycleOwner(this);

        if (uri != null) {
            dataBinding.setUri(uri);
        }


    }
}
