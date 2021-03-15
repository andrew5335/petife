package kr.co.ainus.petife2.apapter;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import kr.co.ainus.petife2.view.fragment.FeedAutoFragment;
import kr.co.ainus.petife2.view.fragment.FeedFreeFragment;
import kr.co.ainus.petife2.view.fragment.FeedManualFragment;
import kr.co.ainus.petica_api.model.domain.Petica;
import kr.co.ainus.petica_api.model.type.FeedType;

public class FeedFragmentAdapter extends FragmentStateAdapter {

    private final Petica petica;
    private final FeedType feedType;

    public FeedFragmentAdapter(@NonNull FragmentActivity fragmentActivity, Petica petica, FeedType feedType) {
        super(fragmentActivity);
        this.petica = petica;
        this.feedType = feedType;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public boolean containsItem(long itemId) {
        return super.containsItem(itemId);
    }

    @NonNull
    @Override
    public Parcelable saveState() {
        return super.saveState();
    }

    @Override
    public void restoreState(@NonNull Parcelable savedState) {
        super.restoreState(savedState);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return FeedManualFragment.newInstance(petica, feedType);
            case 1:
                return FeedAutoFragment.newInstance(petica, feedType);
            case 2:
                return FeedFreeFragment.newInstance(petica, feedType);

            default:
                return FeedManualFragment.newInstance(petica, feedType);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

