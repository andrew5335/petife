package kr.co.ainus.petife2.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.apapter.MainViewPagerAdapter;
import kr.co.ainus.petife2.databinding.ActivityMain2Binding;
import kr.co.ainus.petife2.databinding.HeaderMenuBinding;
import kr.co.ainus.petife2.util.TopicHelper;
import kr.co.ainus.petica_api.model.domain.Petica;
import kr.co.ainus.petife2.handler.BackPressCloseHandler;
import me.relex.circleindicator.CircleIndicator3;

public class MainActivity extends _BaseNavigationActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private ActivityMain2Binding dataBinding;

    private MainViewPagerAdapter mainViewPagerAdapter;

    private BackPressCloseHandler backPressCloseHandler;    // 2020-08-24 by Andrew Kim - 뒤로가기 버튼 2회 클릭 시 앱 종료 처리 추가

    private CircleIndicator3 mIndicator;

    private ViewPager2 mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setDataBinding();
        setViewModel();

        this.backPressCloseHandler = new BackPressCloseHandler(this);    // 2020-08-24 by Andrew Kim - 뒤로가기 처리
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e(TAG, "onStart()");

        petViewModel.loadPet(this);
        peticaViewModel.loadPeticaList(uuid);
        dataBinding.dl.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        //if (dataBinding.dl.isDrawerOpen(GravityCompat.START)) {
        //    dataBinding.dl.closeDrawer(GravityCompat.START);
        //} else {
        this.backPressCloseHandler.onBackPressed();
        //}
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        baseNavigationBinding.tvTitle.setText(getString(R.string.appTitle));
        baseNavigationBinding.btnLeft.setVisibility(View.GONE);
        baseNavigationBinding.btnRight.setText(getString(R.string.addDevice));
        baseNavigationBinding.btnRight.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), PeticaAddActivity.class));
        });

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_main2, baseNavigationBinding.baseNavContainer, true);
        dataBinding.setLifecycleOwner(this);

        if (getSupportActionBar() != null) {
            setSupportActionBar(baseNavigationBinding.tb);
            getSupportActionBar().setTitle("");
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, dataBinding.dl, baseNavigationBinding.tb, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        dataBinding.dl.addDrawerListener(toggle);
        toggle.syncState();
        dataBinding.nv.setNavigationItemSelectedListener(this);

        mainViewPagerAdapter = new MainViewPagerAdapter(this);
        dataBinding.vp2Main.setAdapter(mainViewPagerAdapter);
        dataBinding.vp2Main.setUserInputEnabled(false);

        mPager = findViewById(R.id.vp_2_main);
        mIndicator = findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        //mIndicator.createIndicators(dataBinding.vp2Main.getChildCount()+1,0);

        dataBinding.vp2Main.setPageTransformer((page, position) -> {
//            if (position == 0) {
//                Log.e(TAG, "position = " + dataBinding.vp2Main.getCurrentItem());
//
//                Petica petica = mainViewPagerAdapter.getPeticaList().get(dataBinding.vp2Main.getCurrentItem());
//
//                Log.e(TAG, "peticaId = " + petica.getDeviceId());
//            }
            mIndicator.createIndicators(mainViewPagerAdapter.getPeticaList().size(), 0);
        });

        dataBinding.vp2Main.setCurrentItem(0);

        dataBinding.nv.setNavigationItemSelectedListener(menuItem -> {
            Intent intent;

            switch (menuItem.getItemId()) {
                case R.id.action_pet_setting:
                    intent = new Intent(getApplicationContext(), PetSettingActivity.class);
                    startActivity(intent);
                    break;

                case R.id.action_sign_out:
                    userViewModel.signOut(MainActivity.this);
                    intent = new Intent(getApplicationContext(), SignInActivity.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.action_permission_request_receive:
                    PermissionActivity.permissionType = PermissionActivity.PermissionType.RECEIVE;
                    startActivity(new Intent(getApplicationContext(), PermissionActivity.class));

                    break;

                case R.id.action_permission_request_send:
                    PermissionActivity.permissionType = PermissionActivity.PermissionType.SEND;
                    startActivity(new Intent(getApplicationContext(), PermissionActivity.class));

                    break;
            }
            return false;
        });

        dataBinding.llMessage.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), PeticaAddActivity.class)));

        HeaderMenuBinding headerMenuBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.header_menu, null, false);
        headerMenuBinding.setName(name);
        headerMenuBinding.setEmail(email);

        dataBinding.nv.addHeaderView(headerMenuBinding.getRoot());
    }

    @Override
    public void setViewModel() {
        super.setViewModel();

        peticaViewModel.getPeticaListLiveData().observe(this, peticaList -> {

            Log.d(TAG, "메인 뷰페이저 잠금 설정");
            dataBinding.vp2Main.setUserInputEnabled(false);

            Log.i(TAG, "petica list 에 변화를 감지하였습니다");

            if (peticaList == null) {
                Log.e(TAG, "device list == null");
                return;
            }

            Log.i(TAG, "petica list size = " + peticaList.size());


            if (peticaList.size() < 1) {
                dataBinding.llMessage.setVisibility(View.VISIBLE);
            } else {
                dataBinding.llMessage.setVisibility(View.GONE);
            }

            TopicHelper.allFalse(getApplicationContext());
            Log.i(TAG, "topic 수신 상태 모두 false 처리합니다");

            Log.i(TAG, "petica fragment를 mainViewPager에 추가 시작");
            mainViewPagerAdapter = new MainViewPagerAdapter(this);
            dataBinding.vp2Main.setAdapter(mainViewPagerAdapter);
            mainViewPagerAdapter.setPeticaList(peticaList);
            Log.i(TAG, "petica fragment를 mainViewPager에 추가 완료");

            for (Petica petica : peticaList) {
                Log.d(TAG, "구독 topic SharePreferences 에 입력 시작");
                TopicHelper.putBoolean(getApplicationContext(), petica.getDeviceId(), true);
                Log.d(TAG, "구독 topic SharePreferences 에 입력 완료");
            }

            Set<Map.Entry<String, Boolean>> entrySet = TopicHelper.getBooleans(getApplicationContext()).entrySet();
            for (Map.Entry<String, Boolean> entry : entrySet) {
                if (entry.getValue()) {
                    Log.i(TAG, "subscribe = " + entry.getKey());
                    FirebaseMessaging.getInstance().subscribeToTopic(entry.getKey());
                } else {
                    Log.i(TAG, "unsubscribe = " + entry.getKey());
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(entry.getKey());
                }
            }

            Log.d(TAG, "메인 뷰페이저 잠금 해제");
            dataBinding.vp2Main.setUserInputEnabled(true);
        });

        petViewModel.getPetLiveData().observe(this, pet -> dataBinding.setPet(pet));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        dataBinding.dl.closeDrawer(GravityCompat.START);

        return false;
    }

}

