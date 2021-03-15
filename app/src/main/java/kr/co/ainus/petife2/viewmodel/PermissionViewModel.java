package kr.co.ainus.petife2.viewmodel;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import kr.co.ainus.petica_api.ApiHelper;
import kr.co.ainus.petica_api.model.domain.Permission;
import kr.co.ainus.petica_api.model.response.PermissionResponse;
import kr.co.ainus.petica_api.model.type.PermissionStateType;

public class PermissionViewModel extends ViewModel {
    private final MutableLiveData<Boolean> hasLoadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Permission>> permissionListLIveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getHasLoadingLiveData() {
        return hasLoadingLiveData;
    }

    public MutableLiveData<List<Permission>> getPermissionListLIveData() {
        return permissionListLIveData;
    }

    public void permissionMaster(String uuid, ApiHelper.SuccessHandler successHandler, ApiHelper.FailureHandler failureHandler, ApiHelper.CompleteHandler completeHandler) {
        hasLoadingLiveData.setValue(true);

        ApiHelper.permissionMaster(
                uuid
                , successHandler
                , failureHandler
                , completeHandler);
    }

    public void permissionMaster(String uuid) {
        hasLoadingLiveData.setValue(true);

        ApiHelper.permissionMaster(
                uuid
                , new ApiHelper.SuccessHandler() {
                    @Override
                    public <V> void onSuccess(V response) {

                        if (response instanceof PermissionResponse) {

                            List<Permission> permissionList = ((PermissionResponse) response).getItems();
                            permissionListLIveData.setValue(permissionList);
                        }

                    }
                }
                , new ApiHelper.FailureHandler() {
                    @Override
                    public void onFailure(Throwable t) {

                    }
                }
                , new ApiHelper.CompleteHandler() {
                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void permissionSlave(String uuid, PermissionStateType stateType, ApiHelper.SuccessHandler successHandler, ApiHelper.FailureHandler failureHandler, ApiHelper.CompleteHandler completeHandler) {
        hasLoadingLiveData.setValue(true);

        ApiHelper.permissionSlave(
                uuid
                , stateType
                , successHandler
                , failureHandler
                , completeHandler);
    }

    public void permissionSlave(String uuid, PermissionStateType stateType) {
        hasLoadingLiveData.setValue(true);

        ApiHelper.permissionSlave(
                uuid
                , stateType
                , new ApiHelper.SuccessHandler() {
                    @Override
                    public <V> void onSuccess(V response) {

                        if (response instanceof PermissionResponse) {

                            List<Permission> permissionList = ((PermissionResponse) response).getItems();
                            permissionListLIveData.setValue(permissionList);
                        }
                    }
                }
                , new ApiHelper.FailureHandler() {
                    @Override
                    public void onFailure(Throwable t) {

                    }
                }
                , new ApiHelper.CompleteHandler() {
                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void permissionRequest(String uuid, String masterEmail, ApiHelper.SuccessHandler successHandler, ApiHelper.FailureHandler failureHandler, ApiHelper.CompleteHandler completeHandler) {
        hasLoadingLiveData.setValue(true);

        ApiHelper.permissionRequest(
                uuid
                , masterEmail

                , successHandler
                , failureHandler
                , completeHandler);
    }


    public void permissionAccept(String uuid, long permissionIdx, ApiHelper.SuccessHandler successHandler, ApiHelper.FailureHandler failureHandler, ApiHelper.CompleteHandler completeHandler) {
        hasLoadingLiveData.setValue(true);

        ApiHelper.permissionAccept(
                uuid
                , permissionIdx
                , successHandler
                , failureHandler
                , completeHandler);
    }


    public void permissionDeny(String uuid, long permissionIdx, ApiHelper.SuccessHandler successHandler, ApiHelper.FailureHandler failureHandler, ApiHelper.CompleteHandler completeHandler) {
        hasLoadingLiveData.setValue(true);

        ApiHelper.permissionDeny(
                uuid
                , permissionIdx
                , successHandler
                , failureHandler
                , completeHandler);
    }


    public void permissionCancel(String uuid, long permissionIdx, ApiHelper.SuccessHandler successHandler, ApiHelper.FailureHandler failureHandler, ApiHelper.CompleteHandler completeHandler) {
        hasLoadingLiveData.setValue(true);

        ApiHelper.permissionCancel(
                uuid
                , permissionIdx
                , successHandler
                , failureHandler
                , completeHandler);
    }

}
