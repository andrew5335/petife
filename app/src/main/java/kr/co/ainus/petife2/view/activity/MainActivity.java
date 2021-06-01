package kr.co.ainus.petife2.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager2.widget.ViewPager2;

import com.demo.sdk.Scanner2;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.net.InetAddress;
import java.util.*;

import com.nabto.api.RemoteTunnel;
import kotlin.jvm.internal.TypeReference;
import kr.co.ainus.petica_api.model.type.FeedModeType;
import kr.co.ainus.peticaexcutor.Executor;
import kr.co.ainus.peticaexcutor.callback.FailCallback;
import kr.co.ainus.peticaexcutor.callback.ReceiveCallback;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.apapter.MainViewPagerAdapter;
import kr.co.ainus.petife2.databinding.ActivityMain2Binding;
import kr.co.ainus.petife2.databinding.HeaderMenuBinding;
import kr.co.ainus.petife2.util.RandomPortHelper;
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

    private int randomPortAudio = RandomPortHelper.make();
    private int randomPortVideo = RandomPortHelper.make();
    private int videoType = 0; // 0 h264 else mpeg
    private String peticaIp = Executor.DEVICE_IP;
    private boolean hasStop = false;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private String tmpPeticaListStr = "";
    private Petica[] tmpPeticaListArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setDataBinding();
        setViewModel();

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        this.backPressCloseHandler = new BackPressCloseHandler(this);    // 2020-08-24 by Andrew Kim - 뒤로가기 처리
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e(TAG, "onStart()");

        petViewModel.loadPet(this);
        peticaViewModel.loadPeticaList(uuid);
        dataBinding.dl.closeDrawer(GravityCompat.START);
        //onStartCheckRemote();
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

            // peticaList가 null 일 경우 앱 내 저장소에 저장해둔 리스트를 가져와 사용하도록 처리 추가 2021-05-31 by Andrew Kim
            if (peticaList == null) {
                tmpPeticaListStr = pref.getString("peticaList", "");

                // 앱 내 저장소에도 petife 리스트가 없을 경우는 빈 값 return 2021-05-31 by Andrew Kim
                if(null != tmpPeticaListStr && !"".equals(tmpPeticaListStr) && 0 < tmpPeticaListStr.length()) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        tmpPeticaListArr = mapper.readValue(tmpPeticaListStr, Petica[].class);
                        peticaList = Arrays.asList(mapper.readValue(tmpPeticaListStr, Petica[].class));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "device list == null");
                    return;
                }
            }

            if(null != peticaList && 0 < peticaList.size()) {
                String peticaListStr = "";
                peticaListStr = Arrays.deepToString(peticaList.toArray());
                Log.i(TAG, "===== peticalist string : " + peticaListStr);

                tmpPeticaListStr = pref.getString("peticaList", "");

                // API 통신을 통해 petife 리스트를 가져온 이후 앱 내 저장소에 저장된 petife 리스트가 있는지 확인하여 이미 저장된 리스트가 있을 경우에는
                // 기존 저장된 리스트를 삭제하고 새로 저장하고 없을 경우에는 API 통신을 통해 받아온 값을 바로 저장 처리
                // 2021-05-31 by Andrew Kim
                if(null != tmpPeticaListStr && !"".equals(tmpPeticaListStr) && 0 < tmpPeticaListStr.length()) {
                    /**
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        tmpPeticaListArr = mapper.readValue(tmpPeticaListStr, Petica[].class);
                        peticaList = Arrays.asList(mapper.readValue(tmpPeticaListStr, Petica[].class));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    Log.i(TAG, "===== petife list size 111 : " + peticaList.size());
                     **/
                    editor.remove("peticaList");
                    editor.putString("peticaList", peticaListStr);
                } else {
                    editor.putString("peticaList", peticaListStr);
                }

                editor.apply();
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

            int callCnt = 0;
            callCnt = pref.getInt("callCnt", 0);
            for (Petica petica : peticaList) {
                Log.d(TAG, "구독 topic SharePreferences 에 입력 시작");
                TopicHelper.putBoolean(getApplicationContext(), petica.getDeviceId(), true);
                Log.d(TAG, "구독 topic SharePreferences 에 입력 완료");
                //onStartCheckRemote(petica.getDeviceId());

                if(callCnt == peticaList.size()) {

                } else {
                    checkDateAndSync(petica.getDeviceId());    // 약속된 요일인 경우 (월요일) petife list 만큼 호출하여 현재 live 되어 있는 기기 초기화 진행 2021-05-31 by Andrew Kim
                }
            }

            editor.putInt("callCnt", peticaList.size());
            editor.apply();

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

    void onStartCheckRemote(String deviceId) {

        Log.i(TAG, "=====================start remote check in main activity");
        if(null != mainViewPagerAdapter.getPeticaList() && 0 < mainViewPagerAdapter.getPeticaList().size()) {
            peticaViewModel.openRemoteTunnelVideo(getApplicationContext(), deviceId, randomPortVideo
                    , (id, result) -> {

                        Log.i(TAG, "openRemoteTunnelVideo result = " + result);

                        // TODO Nabto 에러처리 일단 블럭
                        if (result.equals("CONNECT_TIMEOUT") ||
                                result.equals("NTCS_CLOSED") ||
                                result.equals("NTCS_UNKNOWN") ||
                                result.equals("FAILED")) {

                            // RemoteTunnel.closeTunnels();

                            //onStopPlayVideo();
                            //finish();

                            //return;
                        }

                        Log.i(TAG, "=========start check remote result : " + result);
                        if (result.equals("Local")) {
                            // RemoteTunnel.closeTunnels();

                            randomPortVideo = 554;
                            randomPortAudio = 80;

                            Scanner2 scanner2 = new Scanner2(getApplicationContext());
                            scanner2.setOnScanOverListener((map, inetAddress) -> {

                                Log.i(TAG, "scanner2 onresult");

                                boolean hasScan = false;
                                videoType = 2;

                                for (Map.Entry<InetAddress, String> entry : map.entrySet()) {

                                    if (entry.getValue().equals(mainViewPagerAdapter.getPeticaList().get(0).getDeviceId())) {
                                        hasScan = true;
                                        peticaIp = entry.getKey().toString().replace("/", "");

                                        Log.i(TAG, "petica ip = " + peticaIp);
                                        break;
                                    }

                                }

                                if (hasStop) {
                                    Log.e(TAG, "life cycle 이 start 가 아니어서 종료됩니다");
                                    //onStopPlayVideo();
                                    //return;
                                }

                                if (hasScan) {

                                    //onStartPlayVideo();
                                    //onStartPlayAudio(false);

                                } else {

                                    // TODO 펫티카 못찾음 처리
                                    Log.e(TAG, "페티카 못찾음");

                                }

                            });
                            scanner2.scanAll();


                        } else if (result.equals("Remote P2P")) {

                            videoType = 0;
                            //onStartPlayVideo();
                            //onStartPlayAudio(true);    // 2021-01-15 소리 수정

                        }

                    });
        }
    }

    public void checkDateAndSync(String deviceId) {
        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_WEEK);

        // 요일을 확인하여 월요일인 경우 초기화 및 시간 동기화 호출 처리 2021-05-31 by Andrew Kim
        if(today == 2) {

            peticaViewModel.openRemoteTunnelUart(this, deviceId, randomPortAudio, new RemoteTunnel.OnResultListener() {
                @Override
                public void onResult(String id, String result) {

                    Log.i(TAG, "sss = " + result);

                    if (result.equals("Local") || result.equals("Remote P2P")) {

                        peticaViewModel.onExecutePeticaStatusRequest("127.0.0.1", randomPortAudio
                                , null, new FailCallback() {
                                    @Override
                                    public void onFail() {
                                        runOnUiThread(() -> {
                                            //alertFail();
                                        });
                                    }
                                }, null, null
                                , new ReceiveCallback() {
                                    @Override
                                    public void onReceive(byte[] peticaResponse) {

                                    }
                                });

                    } else {

                        Log.e(TAG, "openRemoteTunnel 실패");
                        //alertFail();

                    }

                }
            });

            Calendar calendar = Calendar.getInstance(Locale.KOREA);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);

            peticaViewModel.onExecutePeticaClockSet("127.0.0.1", randomPortAudio, hour, minute, second
                , () -> runOnUiThread(() -> {
                    /**
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle(getString(R.string.syncTimeSuccess))
                            .setPositiveButton(getString(R.string.confirm), null)
                            .create();
                    alertDialog.show();
                     **/
                    Log.i(TAG, "===== sync time success");
                }), () -> runOnUiThread(() -> {
                    /**
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle(getString(R.string.syncTimeFail))
                            .setPositiveButton(getString(R.string.confirm), null)
                            .create();
                    alertDialog.show();
                     **/
                        Log.i(TAG, "===== sync time fail");
                }), null, null, (peticaResponse -> {
                    //dataBinding.setPeticaStatus(peticaViewModel.getStatus(peticaResponse[6]));
                }));

            peticaViewModel.onExecutePeticaInitialization("127.0.0.1", randomPortAudio,
                    () -> {
                        Log.i(TAG, "onSuccess");
                        runOnUiThread(() -> {
                            /**
                            AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                                    .setTitle(getString(R.string.initComplete))
                                    .setPositiveButton(getString(R.string.confirm), null)
                                    .create();
                            alertDialog2.show();
                             **/
                            Log.i(TAG, "===== petife init success");
                        });
                    }
                    , () -> Log.i(TAG, "onFail")
                    , () -> Log.i(TAG, "onComplete")
                    , () -> Log.i(TAG, "onSend")
                    , peticaResponse -> Log.i(TAG, "onReceive"));
        }
    }
}

