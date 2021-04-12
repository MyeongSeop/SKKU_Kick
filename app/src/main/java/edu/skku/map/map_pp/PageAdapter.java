package edu.skku.map.map_pp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class PageAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragmentList;
    ArrayList<String> fragment_titlelist;
    public PageAdapter(@NonNull FragmentManager fm) {
        super(fm);
        fragmentList = new ArrayList<>();
        fragment_titlelist = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }


    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment, String title){
        fragmentList.add(fragment);
        fragment_titlelist.add(title);
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position){
        return fragment_titlelist.get(position);
    }
}
