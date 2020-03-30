package com.example.cyk.cloudweather;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class CityFragmentPagerAdapter extends FragmentStatePagerAdapter {
    List<Fragment> fragmentList;

    public CityFragmentPagerAdapter(FragmentManager fm,List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    //表示ViewPager包含的页数
    int childCount = 0;
    //当ViewPager的页数发生改变时，必须要重写两个函数


    @Override
    public void notifyDataSetChanged() {
        this.childCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        if (childCount > 0){
            childCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);

    }
}
