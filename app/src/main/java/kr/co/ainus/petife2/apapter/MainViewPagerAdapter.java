package kr.co.ainus.petife2.apapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

import kr.co.ainus.petife2.view.fragment.MainViewPagerFragment;
import kr.co.ainus.petica_api.model.domain.Petica;

public class MainViewPagerAdapter extends FragmentStateAdapter {

    private List<Petica> peticaList = new ArrayList<>();

    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public List<Petica> getPeticaList() {
        return peticaList;
    }

    public void setPeticaList(List<Petica> peticaList) {
        this.peticaList.clear();
        this.peticaList = peticaList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return MainViewPagerFragment.newInstance(peticaList.get(position));
    }

    @Override
    public int getItemCount() {
        return peticaList.size();
    }
}
