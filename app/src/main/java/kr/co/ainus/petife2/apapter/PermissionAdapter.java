package kr.co.ainus.petife2.apapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.ItemPermissionBinding;
import kr.co.ainus.petife2.view.activity.PermissionActivity;
import kr.co.ainus.petica_api.model.domain.Permission;

public class PermissionAdapter extends RecyclerView.Adapter<PermissionAdapter.ViewHolder> {

    private List<Permission> permissionList = new ArrayList<>();

    private OnClickPermissionListener onClickPermissionListener;

    public List<Permission> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<Permission> permissionList) {
        this.permissionList = permissionList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding viewDataBinding = DataBindingUtil.inflate(inflater, viewType, parent, false);

        return new ViewHolder(viewDataBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder.viewDataBinding instanceof ItemPermissionBinding) {
            ItemPermissionBinding dataBinding = (ItemPermissionBinding) holder.viewDataBinding;

            Permission permission = permissionList.get(position);

            switch (PermissionActivity.permissionType) {
                case SEND:
                    dataBinding.btnEmail.setText(permission.getMasterEmail());

                    break;

                case RECEIVE:
                    dataBinding.btnEmail.setText(permission.getSlave().getEmail());

                    break;
            }

            switch (permission.getState()) {
                case REQUEST:

                    switch (PermissionActivity.permissionType) {
                        case SEND:
                            dataBinding.tvState.setText("대기 중");

                            break;

                        case RECEIVE:
                            dataBinding.tvState.setText("요청");

                            break;
                    }

                    break;

                case ACCEPT:

                    dataBinding.tvState.setText("수락");

                    break;

                case DENY:

                    dataBinding.tvState.setText("차단");

                    break;
            }

            dataBinding.btnEmail.setOnClickListener(v->{
                if (onClickPermissionListener != null) onClickPermissionListener.onClickPermission(permission);
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_permission;
    }

    @Override
    public int getItemCount() {
        return permissionList.size();
    }

    public void setOnClickPermissionListener(OnClickPermissionListener onClickPermissionListener) {
        this.onClickPermissionListener = onClickPermissionListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ViewDataBinding viewDataBinding;

        public ViewHolder(@NonNull ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());

            this.viewDataBinding = viewDataBinding;
        }
    }

    public interface OnClickPermissionListener {
        public void onClickPermission(Permission permission);
    }
}
