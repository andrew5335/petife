package kr.co.ainus.petife2.apapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.app.ProgressDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;


import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.ItemSsidInfoBinding;
import kr.co.ainus.petife2.model.SsidInfo;

public class SsidInfoListAdapter extends RecyclerView.Adapter<SsidInfoListAdapter.ViewHolder> {

    private static final String TAG = "SsidInfoListAdapter";

    private OnClickSsidInfoListener onClickSsidInfoListener;

    private List<SsidInfo> ssidInfoList = new ArrayList<>();

    public List<SsidInfo> getSsidInfoList() {
        return ssidInfoList;
    }

    public void setSsidInfoList(List<SsidInfo> ssidInfoList) {
        //HashSet<SsidInfo> tmpList = new HashSet<SsidInfo>();
        //for(int i=0; i < ssidInfoList.size(); i++) {
        //    tmpList.add(ssidInfoList.get(i));
        //}

        List<SsidInfo> list2 = new ArrayList<SsidInfo>();
        //list2 = new ArrayList<SsidInfo>(tmpList);

        List<String> ssidList = new ArrayList<String>();

        for(int i=0; i < ssidInfoList.size(); i++) {
            if(null != ssidList && 0 < ssidList.size()) {
                if(!ssidList.contains(ssidInfoList.get(i).getSsid())) {
                    ssidList.add(ssidInfoList.get(i).getSsid());
                    list2.add(ssidInfoList.get(i));
                }
            } else {
                ssidList.add(ssidInfoList.get(i).getSsid());
                list2.add(ssidInfoList.get(i));
            }
        }

        //Collections.sort(ssidInfoList);
        //this.ssidInfoList = ssidInfoList;
        Collections.sort(list2);
        this.ssidInfoList = list2;
        this.notifyDataSetChanged();
    }

    public void setOnClickSsidInfoListener(OnClickSsidInfoListener onClickSsidInfoListener) {
        this.onClickSsidInfoListener = onClickSsidInfoListener;
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
        if (holder.viewDataBinding instanceof ItemSsidInfoBinding) {

            ItemSsidInfoBinding dataBinding = (ItemSsidInfoBinding) holder.viewDataBinding;

            dataBinding.setSsidInfo(ssidInfoList.get(position));
            dataBinding.executePendingBindings();
            dataBinding.btnSsidInfo.setOnClickListener(v -> {
                if (onClickSsidInfoListener != null) onClickSsidInfoListener.onClickSsidInfo(ssidInfoList.get(position));
            });
        }

    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_ssid_info;
    }

    @Override
    public int getItemCount() {
        return ssidInfoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ViewDataBinding viewDataBinding;

        public ViewHolder(@NonNull ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            this.viewDataBinding = viewDataBinding;
        }

        public ViewDataBinding getViewDataBinding() {
            return viewDataBinding;
        }
    }

    public interface OnClickSsidInfoListener {
        public void onClickSsidInfo(SsidInfo ssidInfo);
    }
}
