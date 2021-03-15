package kr.co.ainus.petife2.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikesu.horizontalexpcalendar.view.page.PageView;

import org.joda.time.DateTime;

import java.util.ArrayList;

import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.apapter.PostAdapter;
import kr.co.ainus.petife2.databinding.ActivityPostBinding;
import kr.co.ainus.petife2.model.room.Post;
import kr.co.ainus.petife2.util.MediaHelper;

public class PostActivity extends _BaseNavigationActivity {

    private static final String TAG = "PostActivity";

    private ActivityPostBinding dataBinding;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDataBinding();
        setViewModel();
    }

    @Override
    protected void onStart() {
        super.onStart();

        postViewModel.loadPostList(getApplicationContext(), 0);
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        // TODO 진입 시 날짜 초기화 필요함

        baseNavigationBinding.tvTitle.setText(getString(R.string.petDiary));
        baseNavigationBinding.btnRight.setVisibility(View.GONE);

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_post, baseNavigationBinding.baseNavContainer, true);
        dataBinding.setLifecycleOwner(this);

        dataBinding.rvPost.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        postAdapter = new PostAdapter(new ArrayList<>());
        postAdapter.setOnClickMenuListener(new PostAdapter.OnClickMenuListener() {
            @Override
            public void onClickMenu(Post post) {
                AlertDialog alertDialog = new AlertDialog.Builder(PostActivity.this)
                        .setItems(new String[]{
                                getString(R.string.modify)
                                , getString(R.string.delete2)
                        }, ((dialog, which) -> {
                            switch (which) {
                                case 0:
                                    PostAddActivity.mode = getString(R.string.modify);
                                    PostAddActivity.post = post;

                                    startActivity(new Intent(getApplicationContext(), PostAddActivity.class));

                                    break;

                                case 1:
                                    postViewModel.postRemove(getApplicationContext(), post);
                                    postViewModel.loadPostList(getApplicationContext(), 0);

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

        postAdapter.setOnClickMediaListener(new PostAdapter.OnClickMediaListener() {
            @Override
            public void onClickMedia(Post post) {

                switch (post.getMediaType()) {
                    case MediaHelper.PHOTO:
                        PhotoShowActivity.uri = post.getUri();
                        startActivity(new Intent(getApplicationContext(), PhotoShowActivity.class));
                        break;

                    case MediaHelper.VIDEO:
                        VideoPlayActivity.uri = post.getUri();
                        startActivity(new Intent(getApplicationContext(), VideoPlayActivity.class));

                        break;
                }

            }
        });
        dataBinding.rvPost.setAdapter(postAdapter);
        dataBinding.fabAddPost.setOnClickListener(v -> {
            PostAddActivity.mode = getString(R.string.write);
            PostAddActivity.post = null;

            Intent intent = new Intent(getApplicationContext(), PostAddActivity.class);
            DateTime dateTime = postViewModel.getSelectDateTimeLiveData().getValue();

            if (dateTime != null) {
                intent.putExtra("year", dateTime.getYear());
                intent.putExtra("month", dateTime.getMonthOfYear());
                intent.putExtra("day", dateTime.getDayOfMonth());
            }

            startActivity(intent);
        });

        dataBinding.horizontalExpCalendar.setOnClickDateTimeListener(new PageView.OnClickDateTimeListener() {
            @Override
            public void onClickDateTime(DateTime dateTime) {
                postViewModel.getSelectDateTimeLiveData().setValue(dateTime);
            }
        });

    }

    @Override
    public void setViewModel() {
        super.setViewModel();

        postViewModel.getPostListLiveData().observe(this, postList -> {
            postAdapter.setPostList(postList);
        });

    }
}

