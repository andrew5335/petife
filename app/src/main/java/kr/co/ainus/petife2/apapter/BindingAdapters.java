package kr.co.ainus.petife2.apapter;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import androidx.databinding.BindingAdapter;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petica_api.model.type.FeedModeType;

public class BindingAdapters {

    private static final String TAG = "BindingAdapters";

    @BindingAdapter({"signalLevel"})
    public static void signalLevel(ImageView imageView, int signalLevel) {

        if (signalLevel > -30) {
            imageView.setImageResource(R.drawable.ic_signal_4);

        } else if (signalLevel > -45) {
            imageView.setImageResource(R.drawable.ic_signal_3);

        } else if (signalLevel > -60) {
            imageView.setImageResource(R.drawable.ic_signal_2);

        } else if (signalLevel <= -60) {
            imageView.setImageResource(R.drawable.ic_signal_1);
        }
    }

    @BindingAdapter({"glideUriPet"})
    public static void glideUriPet(ImageView imageView, String url) {
        Log.d(TAG, "glide uri pet = " + url);
        Glide.with(imageView.getContext()).load(url).placeholder(R.drawable.photo_pet).into(imageView);
    }

    @BindingAdapter({"photoUri"})
    public static void glideUri(ImageView imageView, String uriString) {
        if (uriString == null) return;
        Glide.with(imageView.getContext()).load(uriString).placeholder(R.drawable.photo_pet).into(imageView);
    }

    @BindingAdapter({"videoUri"})
    public static void videoUrl(VideoView videoView, String uriString) {
        if (uriString == null) return;
        Uri uri = Uri.parse(uriString);
        videoView.setVideoURI(uri);
        videoView.start();
    }

    @BindingAdapter({"petGender"})
    public static void petGender(TextView textView, String petGender) {
        if (petGender == null) return;

        petGender = petGender.toLowerCase();

        if (petGender.equals("m")) {
            textView.setCompoundDrawables(null, null, textView.getContext().getResources().getDrawable(R.drawable.ic_male), null);
        } else if (petGender.equals("f")) {
            textView.setCompoundDrawables(null, null, textView.getContext().getResources().getDrawable(R.drawable.ic_female), null);
        }
    }

    @BindingAdapter({"feedMode"})
    public static void feedMode(View view, FeedModeType feedModeType) {
        if (feedModeType == null) return;

        String msg = null;
        switch (feedModeType) {
            case MANUAL:
                msg = "수동";
                break;

            case FREE:
                msg = "자율";
                break;

            case AUTO:
                msg = "예약";
                break;
        }

        if (view instanceof Button) {
            ((Button) view).setText(msg);
        } else if (view instanceof TextView) {
            ((TextView) view).setText(msg);
        }
    }
}
