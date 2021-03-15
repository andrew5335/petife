package kr.co.ainus.petife2.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.ActivityVideoPlayBinding;

public class VideoPlayActivity extends AppCompatActivity {

    public static String uri;

    private MediaController mediaController;
    private ActivityVideoPlayBinding dataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        if (getActionBar() != null) getActionBar().hide();

        mediaController = new MediaController(this);

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_video_play);
        dataBinding.setLifecycleOwner(this);

        if (uri != null) {
            dataBinding.setUri(uri);
        }

        dataBinding.vv.setMediaController(mediaController);
    }
}
