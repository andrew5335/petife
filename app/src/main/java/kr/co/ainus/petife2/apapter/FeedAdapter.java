package kr.co.ainus.petife2.apapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.ItemFeedBinding;
import kr.co.ainus.petica_api.model.domain.Feed;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private final List<Feed> feedList;
    private OnClickListener onClickListener;
    private OnLongClickListener onLongClickListener;

    public FeedAdapter(List<Feed> feedList) {
        Collections.sort(feedList);
        this.feedList = feedList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding viewDataBinding = DataBindingUtil.inflate(inflater, R.layout.item_feed, parent, false);

        return new ViewHolder(viewDataBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemFeedBinding itemFeedBinding = (ItemFeedBinding) holder.viewDataBinding;
        itemFeedBinding.setFeed(feedList.get(position));
        itemFeedBinding.executePendingBindings();

        itemFeedBinding.btnFeed.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onLongClickListener != null) {
                    onLongClickListener.onLongClick(feedList.get(position), position);
                }
                return false;
            }
        });

        itemFeedBinding.btnFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(feedList.get(position), position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public void addItem(Feed feed) {
        this.feedList.add(feed);
        Collections.sort(this.feedList);

        this.notifyDataSetChanged();
    }

    public void removeItem(int index) {
        this.feedList.remove(index);
        Collections.sort(this.feedList);

        this.notifyDataSetChanged();
    }

    public List<Feed> getItems() {
        return this.feedList;
    }

    public void setitems(List<Feed> feedList) {
        Collections.sort(feedList);
        this.feedList.clear();
        this.feedList.addAll(feedList);


        this.notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ViewDataBinding viewDataBinding;

        public ViewHolder(@NonNull ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            this.viewDataBinding = viewDataBinding;
        }
    }

    public void addOnClickLIstner(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void addOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public interface OnClickListener {
        void onClick(Feed feed, int position);
    }

    public interface OnLongClickListener {
        void onLongClick(Feed feed, int position);
    }
}
