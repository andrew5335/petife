package kr.co.ainus.petife2.apapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.ItemPostBinding;
import kr.co.ainus.petife2.model.room.Post;
import kr.co.ainus.petife2.util.MediaHelper;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List<Post> postList;
    private OnClickMenuListener onClickMenuListener;
    private OnClickMediaListener onClickMediaListener;

    public PostAdapter(@NonNull List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), viewType, parent, false);
        return new PostAdapter.ViewHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder.viewDataBinding instanceof ItemPostBinding) {

            if (null != postList && 0 < postList.size()) {
                Post post = postList.get(position);

                ItemPostBinding dataBinding = (ItemPostBinding) holder.viewDataBinding;

                dataBinding.setPost(post);
                dataBinding.executePendingBindings();

                if (null != post) {
                    if (null != post.getUri() && !post.getUri().isEmpty()) {    // null check 추가 2021-03-16 by Andrew Kim
                        switch (post.getMediaType()) {
                            case MediaHelper.PHOTO:
                                dataBinding.iv.setOnClickListener(v -> {
                                    if (onClickMediaListener != null) {
                                        onClickMediaListener.onClickMedia(post);
                                    }
                                });

                                break;
                            case MediaHelper.VIDEO:
                                dataBinding.ibVideoPlay.setOnClickListener(v -> {
                                    if (onClickMediaListener != null) {
                                        onClickMediaListener.onClickMedia(post);
                                    }
                                });
                                break;

                        }
                    }
                }

                dataBinding.btnMore.setOnClickListener(v -> {
                    if (onClickMenuListener != null) onClickMenuListener.onClickMenu(post);
                });
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        return R.layout.item_post;
    }

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return postList.size();
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

    public void setOnClickMenuListener(OnClickMenuListener onClickMenuListener) {
        this.onClickMenuListener = onClickMenuListener;
    }

    public void setOnClickMediaListener(OnClickMediaListener onClickMediaListener) {
        this.onClickMediaListener = onClickMediaListener;
    }

    public interface OnClickMenuListener {
        public void onClickMenu(Post post);
    }

    public interface OnClickMediaListener {
        public void onClickMedia(Post post);
    }
}


