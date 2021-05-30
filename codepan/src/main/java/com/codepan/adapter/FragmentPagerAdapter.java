package com.codepan.adapter;

import com.codepan.app.CPFragment;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


public class FragmentPagerAdapter extends FragmentStatePagerAdapter {

	private final List<CPFragment> fragments;
	private String[] tabItems;

	public FragmentPagerAdapter(FragmentManager fm, List<CPFragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	public FragmentPagerAdapter(FragmentManager fm, List<CPFragment> fragments, String[] tabItems) {
		super(fm);
		this.fragments = fragments;
		this.tabItems = tabItems;
	}

	@Override
	public Fragment getItem(int position) {
		return this.fragments.get(position);
	}

	@Override
	public int getCount() {
		return this.fragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return tabItems[position];
	}
}
