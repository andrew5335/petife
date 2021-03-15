package kr.co.ainus.petife2.viewmodel;

import android.content.Context;
import android.util.Log;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.joda.time.DateTime;

import kr.co.ainus.petife2.model.room.Post;
import kr.co.ainus.petife2.model.room.PostDatabase;
import kr.co.ainus.petife2.util.GsonHelper;

public class PostViewModel extends ViewModel {
    private static final String TAG = "PostViewModel";

    private final MutableLiveData<List<Post>> postListLiveData = new MutableLiveData<>();
    private final MutableLiveData<DateTime> selectDateTimeLiveData = new MutableLiveData<>();

    public MutableLiveData<List<Post>> getPostListLiveData() {
        return postListLiveData;
    }

    public MutableLiveData<DateTime> getSelectDateTimeLiveData() {
        return selectDateTimeLiveData;
    }

    public void loadPostList(Context context, long userIdx) {
        Log.i(TAG, GsonHelper.getGson().toJson(PostDatabase.getInstance(context).getDao().list(userIdx)));
        postListLiveData.setValue(PostDatabase.getInstance(context).getDao().list(userIdx));
    }

    public void postAdd(Context context, Post post) {
        Log.i(TAG, GsonHelper.getGson().toJson(post));
        PostDatabase.getInstance(context).getDao().insert(post);
    }

    public void postUpdate(Context context, Post post) {
        Log.i(TAG, GsonHelper.getGson().toJson(post));
        PostDatabase.getInstance(context).getDao().update(post);
    }

    public void postRemove(Context context, Post post) {
        Log.i(TAG, GsonHelper.getGson().toJson(post));
        PostDatabase.getInstance(context).getDao().delete(post);
    }

}
