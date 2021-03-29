package kr.co.ainus.petife2.view.fragment;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.apapter.SsidInfoListAdapter;
import kr.co.ainus.petife2.databinding.FragmentPeticaAdd1Binding;
import kr.co.ainus.petife2.model.SsidInfo;
import kr.co.ainus.petife2.view.dialog.WifiPasswordDialog;

public class PeticaAdd1Fragment extends _BaseFragment {

    private static final String TAG = "PeticaAdd1Fragment";

    private FragmentPeticaAdd1Binding dataBinding;
    private SsidInfoListAdapter ssidInfoListAdapter;
    private ProgressDialog progressDialog;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDataBinding();
        setViewModel();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (peticaViewModel.getDeviceAddStepLiveData().getValue() == null) return;

        if (peticaViewModel.getDeviceAddStepLiveData().getValue() == 1) {
            peticaViewModel.loadSsidList("192.168.100.1:80", "admin");
        }
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_petica_add_1, baseDataBinding.baseFragmentContainer, true);
        dataBinding.setLifecycleOwner(this);

        dataBinding.rvAccessPoint.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        dataBinding.rvAccessPoint.setHasFixedSize(true);

        ssidInfoListAdapter = new SsidInfoListAdapter();
        ssidInfoListAdapter.setOnClickSsidInfoListener(new SsidInfoListAdapter.OnClickSsidInfoListener() {
            @Override
            public void onClickSsidInfo(SsidInfo ssidInfo) {

                //WifiManager wifiManager = (WifiManager)getContext().getSystemService(Context.WIFI_SERVICE);
                //wifiManager.disconnect();
                //wifiManager.disableNetwork(wifiManager.getConnectionInfo().getNetworkId());

                if (getActivity() != null) {
                    WifiPasswordDialog wifiPasswordDialog = new WifiPasswordDialog(getActivity(), ssidInfo);
                    wifiPasswordDialog.setCancelable(false);
                    wifiPasswordDialog.setOnClickConfirmButton(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.Q)
                        @Override
                        public void onClick(View v) {
                            //final String PASSWORD = ssidInfo.getPassword() == null || ssidInfo.getPassword().isEmpty() ? "" : ssidInfo.getPassword().trim();
                            EditText passWord2 = wifiPasswordDialog.findViewById(R.id.et_password);
                            TextView selectSsid = wifiPasswordDialog.findViewById(R.id.tv_ssid);
                            final String PASSWORD = passWord2.getText().toString() == null || passWord2.getText().toString().isEmpty() ? "" : passWord2.getText().toString();
                            final String SSID = selectSsid.getText().toString();
                            ssidInfo.setSsid(SSID);
                            ssidInfo.setPassword(PASSWORD);
                            peticaViewModel.peticaJoinWifi("192.168.100.1", "admin", ssidInfo.getSsid(), PASSWORD);

                            Log.i(TAG, "=======password : " + PASSWORD);
                            Log.i(TAG, "=======ssid : " + ssidInfo.getSsid());
                            Log.i(TAG, "=======ssid2 : " + SSID);

                            if(Build.VERSION.SDK_INT >= 30) {
                                /** wifi 자동연결 임시 차단 2021-02-24 by Andrew Kim
                                WifiManager wifiManager = (WifiManager)getContext().getSystemService(Context.WIFI_SERVICE);
                                //wifiManager.disconnect();
                                //wifiManager.disableNetwork(wifiManager.getConnectionInfo().getNetworkId());
                                String curSsid = wifiManager.getConnectionInfo().getSSID();
                                WifiNetworkSpecifier wifiNetworkSpecifier2 = new WifiNetworkSpecifier.Builder()
                                        .setSsid(curSsid)
                                        //.setSsidPattern(new PatternMatcher(ssidInfo.getSsid(), PatternMatcher.PATTERN_PREFIX))
                                        .setWpa2Passphrase(PASSWORD)
                                        .build();

                                NetworkRequest networkRequest2 = new NetworkRequest.Builder()
                                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                                        .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                                        .setNetworkSpecifier(wifiNetworkSpecifier2)
                                        .build();

                                ConnectivityManager connectivityManager = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                                connectivityManager.requestNetwork(networkRequest2, networkCallback2);
                                connectivityManager.unregisterNetworkCallback(networkCallback2);

                                WifiNetworkSpecifier wifiNetworkSpecifier = new WifiNetworkSpecifier.Builder()
                                        .setSsid(ssidInfo.getSsid())
                                        //.setSsidPattern(new PatternMatcher(ssidInfo.getSsid(), PatternMatcher.PATTERN_PREFIX))
                                        .setWpa2Passphrase(PASSWORD)
                                        .build();

                                NetworkRequest networkRequest = new NetworkRequest.Builder()
                                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                                        .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                                        .setNetworkSpecifier(wifiNetworkSpecifier)
                                        .build();

                                connectivityManager.requestNetwork(networkRequest, networkCallback);

                                if(networkCallback != null) {
                                    Log.i(TAG, "========================================NETWORKCALLBACK IS NOT NULL");
                                    //connectivityManager.unregisterNetworkCallback(networkCallback);
                                }
                                **/

                                peticaViewModel.getSelectSsidInfoLiveData().setValue(ssidInfo);
                                wifiPasswordDialog.dismiss();

                                Toast.makeText(getContext(), SSID + "에 연결해주세요.", Toast.LENGTH_LONG).show();
                                Intent callGPSSettingIntent = new Intent(
                                        Settings.ACTION_WIFI_SETTINGS);
                                startActivity(callGPSSettingIntent);

                                //connectivityManager.unregisterNetworkCallback(networkCallback);

                            } else {
                                /** wifi 자동연결 임시 차단 2021-02-24 by Andrew Kim
                                WifiConfiguration conf = new WifiConfiguration();
                                conf.SSID = "\"" + ssidInfo.getSsid() + "\"";
                                conf.wepKeys[0] = "\"" + PASSWORD + "\"";
                                conf.preSharedKey = "\""+ PASSWORD +"\"";
                                conf.wepTxKeyIndex = 0;
                                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

                                WifiManager wifiManager = (WifiManager)getContext().getSystemService(Context.WIFI_SERVICE);
                                wifiManager.getScanResults().get(0).frequency = 2400;
                                int netId = wifiManager.addNetwork(conf);

                                try {
                                    //    List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

                                    //for (WifiConfiguration i : list) {
                                    Log.i(TAG, "======petife wifissid : " + conf.SSID);
                                    //    Log.i(TAG, "======petife wifi i : " + i.networkId);
                                    Log.i(TAG, "======petife netId : " + netId);
                                    //    if (i.SSID != null && i.SSID.equals("\"" + ssidInfo.getSsid() + "\"")) {
                                    wifiManager.disconnect();
                                    wifiManager.disableNetwork(wifiManager.getConnectionInfo().getNetworkId());
                                    try {
                                        Thread.sleep(1000);
                                    } catch(Exception e) {
                                        e.printStackTrace();
                                    }
                                    //wifiManager.enableNetwork(i.networkId, true);
                                    wifiManager.enableNetwork(netId, true);
                                    wifiManager.reconnect();

                                    //break;
                                    //}
                                    //}
                                } catch(SecurityException e) {
                                    e.toString();
                                    Log.e(TAG, "======wifi error : " + e.toString());
                                }
                                 **/



                                peticaViewModel.getSelectSsidInfoLiveData().setValue(ssidInfo);
                                wifiPasswordDialog.dismiss();

                                Toast.makeText(getContext(), SSID + "에 연결해주세요.", Toast.LENGTH_LONG).show();
                                Intent callGPSSettingIntent = new Intent(
                                        Settings.ACTION_WIFI_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }


                            //peticaViewModel.getSelectSsidInfoLiveData().setValue(ssidInfo);
                            //wifiPasswordDialog.dismiss();
                        }
                    });

                    wifiPasswordDialog.setOnClickCancelButton(v -> wifiPasswordDialog.dismiss());

                    try {

                        wifiPasswordDialog.show();

                    } catch (Exception e) {
                        Log.e(TAG, e.getLocalizedMessage());
                        e.printStackTrace();
                    }

                }

            }
        });

        dataBinding.rvAccessPoint.setAdapter(ssidInfoListAdapter);

    }

    @Override
    public void setViewModel() {
        super.setViewModel();

        peticaViewModel.getDeviceAddStepLiveData().observe(this, step -> {

            if (step == null) return;

            if (step == 1) {

                if(getActivity() != null) {
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage(getString(R.string.searchWifi));

                    try {
                        progressDialog.show();
                    } catch (Exception e) {
                        Log.e(TAG, e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                }

                peticaViewModel.loadSsidList("192.168.100.1:80", "admin");

                peticaViewModel.getSsidInfoListLiveData().observe(this, ssidInfoList -> {

                    if (ssidInfoList == null) return;

                    Log.i(TAG, "ssid list 변화");
                    ssidInfoListAdapter.setSsidInfoList(ssidInfoList);
                    if (progressDialog != null)  {
                        try {
                            Thread.sleep(1500);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                });

                peticaViewModel.getHasSuccessPeticaJoinWifi().observe(this, hasSuccessPeticaJoinWifi -> {

                    if (hasSuccessPeticaJoinWifi == null) return;

                    if (hasSuccessPeticaJoinWifi) {

                        peticaViewModel.getDeviceAddStepLiveData().setValue(2);
                        peticaViewModel.getHasSuccessPeticaJoinWifi().setValue(true);

                    } else {

                        try {

                            new AlertDialog.Builder(getActivity())
                                    .setTitle(getString(R.string.error))
                                    .setMessage(getString(R.string.trylater))
                                    .create()
                                    .show();

                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                            e.printStackTrace();
                        }

                    }

                });

            } else {

                Log.i(TAG, "observer 제거");
                peticaViewModel.getSsidInfoListLiveData().removeObservers(this);
                peticaViewModel.getHasSuccessPeticaJoinWifi().removeObservers(this);

            }

        });

    }

    ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {

            Log.d(TAG, "onAvailable");
            try {
                //Thread.sleep(500);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUnavailable() {
            Log.d(TAG, "onUnavailable");
        }
    };

    ConnectivityManager.NetworkCallback networkCallback2 = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            Log.d(TAG, "onAvailable");
        }

        @Override
        public void onUnavailable() {
            Log.d(TAG, "onUnavailable");
        }

        /**
         private boolean isNetworkAvailable() {
         boolean result = false;

         ConnectivityManager connectivityManager = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

         return result;
         }
         **/
    };

}

