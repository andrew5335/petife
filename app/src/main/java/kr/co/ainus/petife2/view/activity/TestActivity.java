package kr.co.ainus.petife2.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;

import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.ActivityTest2Binding;
import kr.co.ainus.petife2.util.GsonHelper;
import kr.co.ainus.petife2.viewmodel.PeticaViewModel;
import kr.co.ainus.petica_api.ApiHelper;
import kr.co.ainus.petica_api.model.type.FeedModeType;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "TestActivity";

    private ActivityTest2Binding dataBinding;

    private PeticaViewModel peticaViewModel;

    private String uuid = "b708c1b9-ca22-443a-8c02-f5c82ac7f4d2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_test2);

        peticaViewModel = ViewModelProviders.of(this).get(PeticaViewModel.class);

        dataBinding.btnAdd.setOnClickListener(v -> {
            String deviceId = dataBinding.etId.getText().toString();
            String devicePw = "test_password";

            peticaViewModel.peticaAddToServer(uuid, deviceId, devicePw, FeedModeType.MANUAL, new ApiHelper.SuccessHandler() {
                @Override
                public <V> void onSuccess(V response) {
                    Log.d(TAG, GsonHelper.getGson().toJson(response));
                }
            }, new ApiHelper.FailureHandler() {
                @Override
                public void onFailure(Throwable t) {
                    Log.e(TAG, "onFailure");
                    Log.e(TAG, t.getLocalizedMessage());
                    t.printStackTrace();
                }
            }, new ApiHelper.CompleteHandler() {
                @Override
                public void onComplete() {
                    Log.d(TAG, "peticaAddToServer onComplete");
                }
            });
        });

        dataBinding.btnRemove.setOnClickListener(v -> {
            String deviceId = dataBinding.etId.getText().toString();

            peticaViewModel.peticaRemoveFromServer(uuid, "lx520.cffa7f.p2p.rakwireless.com", new ApiHelper.SuccessHandler() {
                @Override
                public <V> void onSuccess(V response) {
                    Log.d(TAG, GsonHelper.getGson().toJson(response));
                }
            }, new ApiHelper.FailureHandler() {
                @Override
                public void onFailure(Throwable t) {
                    Log.e(TAG, "onFailure");
                    Log.e(TAG, t.getLocalizedMessage());
                    t.printStackTrace();
                }
            }, new ApiHelper.CompleteHandler() {
                @Override
                public void onComplete() {
                    Log.d(TAG, "peticaRemoveFromServer onComplete");
                }
            });

        });
    }
}
