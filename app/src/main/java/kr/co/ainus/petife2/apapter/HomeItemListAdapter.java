package kr.co.ainus.petife2.apapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nabto.api.RemoteTunnel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.ItemHomePeticaBinding;
import kr.co.ainus.petife2.util.GsonHelper;
import kr.co.ainus.petife2.view.activity.PeticaCameraActivity;
import kr.co.ainus.petica_api.model.domain.Petica;
import kr.co.ainus.petica_api.model.type.FeedModeType;

public class HomeItemListAdapter extends RecyclerView.Adapter<HomeItemListAdapter.ViewHolder> {

    private static final String TAG = "HomeItemListAdapter";

    private List<Petica> peticaList;

    public HomeItemListAdapter(List<Petica> peticaList) {
        this.peticaList = peticaList;
    }

    public List<Petica> getPeticaList() {
        return peticaList;
    }

    public void setPeticaList(List<Petica> peticaList) {
        this.peticaList = peticaList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        ViewDataBinding viewDataBinding = DataBindingUtil.inflate(inflater, viewType, parent, false);

        return new HomeItemListAdapter.ViewHolder(viewDataBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Petica petica = peticaList.get(position);

        if (holder.viewDataBinding instanceof ItemHomePeticaBinding) {
            ItemHomePeticaBinding dataBinding = (ItemHomePeticaBinding) holder.viewDataBinding;
            dataBinding.setPetica(petica);
            dataBinding.executePendingBindings();

            Context context = holder.itemView.getContext();

            dataBinding.btnConnect.setEnabled(false);
            dataBinding.btnConnect.setText("연결 확인 중");
            dataBinding.pb.setVisibility(View.VISIBLE);

            RemoteTunnel remoteTunnel = new RemoteTunnel(context);
            remoteTunnel.openTunnel(petica.getDeviceId(), (int) (Math.random() * 55536) + 10000, 554, petica.getDeviceId(), RemoteTunnel.TunnelType.AUDIO);
            remoteTunnel.setOnResultListener(new RemoteTunnel.OnResultListener() {
                @Override
                public void onResult(String id, String result) {
                    Log.i(TAG, "remote tunner open result = " + result);

                    if (result.equals("Local") || result.equals("Remote P2P")) {
                        dataBinding.btnConnect.setEnabled(true);
                        dataBinding.btnConnect.setText("연결하기");

                    } else {
                        dataBinding.btnConnect.setText("통신 상태를 확인하세요");
                    }

                    dataBinding.pb.setVisibility(View.GONE);

                    remoteTunnel.closeTunnels();
                }
            });

            if (petica.getFeedMode() == FeedModeType.MANUAL) {
                dataBinding.llFeedInfo.setVisibility(View.GONE);
            } else {
                dataBinding.llFeedInfo.setVisibility(View.VISIBLE);
            }

            dataBinding.btnConnect.setOnClickListener(v -> {
                String peticaJson = GsonHelper.getGson().toJson(peticaList.get(position));

                Intent intent = new Intent(holder.itemView.getContext(), PeticaCameraActivity.class);
                intent.putExtra("peticaJson", peticaJson);
                holder.itemView.getContext().startActivity(intent);
            });

            dataBinding.btnMore.setOnClickListener(v -> {
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setItems(new String[]{
                                "펫티카 설정", "식사 설정"
                        }, (dialog, which) -> {
                            switch (which) {
                                case 0:


                                    Toast.makeText(context, "펫티카 설정 클릭", Toast.LENGTH_SHORT).show();
                                    break;
                                case 1:
                                    Toast.makeText(context, "식사 설정 클릭", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }).setNegativeButton("닫기", (dialog, which) -> dialog.dismiss())
                        .create();
                alertDialog.show();
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_home_petica;
    }

    @Override
    public int getItemCount() {
        return peticaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding viewDataBinding;

        public ViewHolder(@NonNull ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());

            this.viewDataBinding = viewDataBinding;
        }

        public ViewDataBinding getViewDataBinding() {
            return viewDataBinding;
        }
    }
}
