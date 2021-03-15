package kr.co.ainus.petife2.view.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.apapter.PermissionAdapter;
import kr.co.ainus.petife2.databinding.ActivityPermissionBinding;
import kr.co.ainus.petife2.util.GsonHelper;
import kr.co.ainus.petife2.view.dialog.PermissionRequestDialog;
import kr.co.ainus.petica_api.ApiHelper;
import kr.co.ainus.petica_api.model.domain.Permission;
import kr.co.ainus.petica_api.model.response.PermissionResponse;
import kr.co.ainus.petica_api.model.type.PermissionStateType;

public class PermissionActivity extends _BaseNavigationActivity {

    private static final String TAG = "PermissionActivity";

    public static PermissionType permissionType;

    private ActivityPermissionBinding dataBinding;
    private PermissionAdapter permissionAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDataBinding();
        setViewModel();
    }

    @Override
    protected void onStart() {
        super.onStart();

        switch (permissionType) {

            case SEND:

                permissionViewModel.permissionSlave(
                        uuid
                        , PermissionStateType.REQUEST);

                break;

            case RECEIVE:

                permissionViewModel.permissionMaster(
                        uuid
                        , new ApiHelper.SuccessHandler() {
                            @Override
                            public <V> void onSuccess(V response) {

                                Log.i(TAG, GsonHelper.getGson().toJson(response));

                                if (response instanceof PermissionResponse) {
                                    permissionAdapter.setPermissionList(((PermissionResponse) response).getItems());
                                    dataBinding.rvPermission.setAdapter(permissionAdapter);
                                }

                            }
                        }, new ApiHelper.FailureHandler() {
                            @Override
                            public void onFailure(Throwable t) {

                            }
                        }, new ApiHelper.CompleteHandler() {
                            @Override
                            public void onComplete() {

                            }
                        });

                break;
        }
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        permissionAdapter = new PermissionAdapter();

        switch (permissionType) {
            case SEND:
                baseNavigationBinding.btnRight.setText(getString(R.string.invite));
                baseNavigationBinding.btnRight.setOnClickListener(v -> {

                    PermissionRequestDialog permissionRequestDialog = new PermissionRequestDialog(PermissionActivity.this);
                    permissionRequestDialog.setOnClickPermissionRequestListner(new PermissionRequestDialog.OnClickPermissionRequestListner() {
                        @Override
                        public void onClickPermissionRequest(Dialog dialog, String email) {
                            dialog.dismiss();
                            permissionViewModel.permissionRequest(uuid, email, new ApiHelper.SuccessHandler() {
                                @Override
                                public <V> void onSuccess(V response) {

                                    Log.i(TAG, GsonHelper.getGson().toJson(response));
                                    permissionViewModel.permissionSlave(uuid, PermissionStateType.REQUEST);

                                }
                            }, new ApiHelper.FailureHandler() {
                                @Override
                                public void onFailure(Throwable t) {

                                }
                            }, new ApiHelper.CompleteHandler() {
                                @Override
                                public void onComplete() {

                                }
                            });
                        }
                    });

                    try {

                        permissionRequestDialog.show();

                    } catch (Exception e) {
                        Log.e(TAG, e.getLocalizedMessage());
                        e.printStackTrace();
                    }


                });
                baseNavigationBinding.tvTitle.setText(getString(R.string.inviteFriend));
                permissionAdapter.setOnClickPermissionListener(new PermissionAdapter.OnClickPermissionListener() {
                    public void onClickPermission(Permission permission) {
                        AlertDialog alertDialog = new AlertDialog.Builder(PermissionActivity.this)
                                .setItems(new String[]{getString(R.string.delete2)}, ((dialog, which) -> {
                                    switch (which) {
                                        case 0:
                                            permissionViewModel.permissionCancel(
                                                    uuid
                                                    , permission.getIdx()
                                                    , new ApiHelper.SuccessHandler() {
                                                        @Override
                                                        public <V> void onSuccess(V response) {

                                                            Log.i(TAG, GsonHelper.getGson().toJson(response));
                                                            permissionViewModel.permissionSlave(uuid, PermissionStateType.REQUEST);

                                                        }
                                                    }
                                                    , new ApiHelper.FailureHandler() {
                                                        @Override
                                                        public void onFailure(Throwable t) {

                                                        }
                                                    }, new ApiHelper.CompleteHandler() {
                                                        @Override
                                                        public void onComplete() {

                                                        }
                                                    }
                                            );
                                            break;

                                        case 1:

                                            break;
                                    }
                                }))
                                .setNegativeButton("닫기", null)
                                .create();

                        try {

                            alertDialog.show();

                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                            e.printStackTrace();
                        }

                    }
                });
                break;

            case RECEIVE:
                baseNavigationBinding.btnRight.setVisibility(View.GONE);
                baseNavigationBinding.tvTitle.setText(getString(R.string.manageFriend));
                permissionAdapter.setOnClickPermissionListener(new PermissionAdapter.OnClickPermissionListener() {
                    @Override
                    public void onClickPermission(Permission permission) {
                        AlertDialog alertDialog = new AlertDialog.Builder(PermissionActivity.this)
                                .setItems(new String[]{getString(R.string.control)}, ((dialog, which) -> {
                                    switch (which) {
                                        case 0:
                                            permissionViewModel.permissionAccept(uuid, permission.getIdx()
                                                    , new ApiHelper.SuccessHandler() {
                                                        @Override
                                                        public <V> void onSuccess(V response) {

                                                            Log.i(TAG, GsonHelper.getGson().toJson(response));

                                                            permissionViewModel.permissionMaster(uuid);

                                                        }
                                                    }, new ApiHelper.FailureHandler() {
                                                        @Override
                                                        public void onFailure(Throwable t) {

                                                        }
                                                    }, new ApiHelper.CompleteHandler() {
                                                        @Override
                                                        public void onComplete() {

                                                        }
                                                    });

                                            break;

                                        case 1:
                                            permissionViewModel.permissionDeny(uuid, permission.getIdx()
                                                    , new ApiHelper.SuccessHandler() {
                                                        @Override
                                                        public <V> void onSuccess(V response) {

                                                            Log.i(TAG, GsonHelper.getGson().toJson(response));

                                                            permissionViewModel.permissionMaster(uuid);

                                                        }
                                                    }, new ApiHelper.FailureHandler() {
                                                        @Override
                                                        public void onFailure(Throwable t) {

                                                        }
                                                    }, new ApiHelper.CompleteHandler() {
                                                        @Override
                                                        public void onComplete() {

                                                        }
                                                    });

                                            break;

                                        case 2:
                                            permissionViewModel.permissionCancel(
                                                    uuid
                                                    , permission.getIdx()
                                                    , new ApiHelper.SuccessHandler() {
                                                        @Override
                                                        public <V> void onSuccess(V response) {

                                                            Log.i(TAG, GsonHelper.getGson().toJson(response));
                                                            permissionViewModel.permissionMaster(uuid);

                                                        }
                                                    }
                                                    , new ApiHelper.FailureHandler() {
                                                        @Override
                                                        public void onFailure(Throwable t) {

                                                        }
                                                    }, new ApiHelper.CompleteHandler() {
                                                        @Override
                                                        public void onComplete() {

                                                        }
                                                    }
                                            );
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

                    }
                });
                break;
        }

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_permission, baseNavigationBinding.baseNavContainer, true);
        dataBinding.setLifecycleOwner(this);

        dataBinding.rvPermission.setLayoutManager(new LinearLayoutManager(this));
        dataBinding.rvPermission.setAdapter(permissionAdapter);
    }

    @Override
    public void setViewModel() {
        super.setViewModel();

        permissionViewModel.getPermissionListLIveData().observe(this, permissionList -> {
            permissionAdapter.setPermissionList(permissionList);
            dataBinding.rvPermission.setAdapter(permissionAdapter);
        });


    }

    public enum PermissionType {
        RECEIVE, SEND
    }
}

