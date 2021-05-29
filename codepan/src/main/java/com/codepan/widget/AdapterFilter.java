package com.codepan.widget;

import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.codepan.model.EntityData;

import java.util.ArrayList;

public class AdapterFilter<T extends EntityData> extends Filter {

	private ArrayAdapter<T> adapter;
	private ArrayList<T> allItems;
	private ArrayList<T> items;

	public AdapterFilter(ArrayList<T> items, ArrayAdapter<T> adapter) {
		this.allItems = new ArrayList<>();
		this.allItems.addAll(items);
		this.adapter = adapter;
		this.items = items;
	}

	@Override
	public CharSequence convertResultToString(Object result) {
		return ((EntityData) result).name;
	}

	@Override
	protected FilterResults performFiltering(CharSequence cs) {
		FilterResults results = new FilterResults();
		if(cs != null) {
			ArrayList<T> suggestList = new ArrayList<>();
			for(T entity : allItems) {
				String name = entity.name;
				String code = entity.code;
				String text1 = name != null ? name.toLowerCase() : null;
				String text2 = code != null ? code.toLowerCase() : null;
				String text3 = null;
				ArrayList<String> codeList = entity.codeList;
				if(codeList != null) {
					StringBuilder sb = new StringBuilder();
					for(String c : codeList) {
						if(c != null) {
							if(codeList.indexOf(c) < codeList.size() - 1) {
								sb.append(c.toLowerCase());
								sb.append(" ");
							}
							else {
								sb.append(c.toLowerCase());
							}
						}
					}
					text3 = sb.toString();
				}
				String search = cs.toString().toLowerCase();
				if((text1 != null && text1.contains(search)) ||
						(text2 != null && text2.contains(search) ||
								(text3 != null && text3.contains(search)))) {
					suggestList.add(entity);
				}
			}
			results.values = suggestList;
			results.count = suggestList.size();
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void publishResults(CharSequence cs, FilterResults fr) {
		items.clear();
		if(fr != null && fr.count > 0) {
			items.addAll((ArrayList<T>) fr.values);
		}
		adapter.notifyDataSetChanged();
	}

	public ArrayList<T> getAllItems() {
		return this.allItems;
	}

	public void clear() {
		filter("");
	}
}
