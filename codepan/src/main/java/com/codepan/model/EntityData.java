package com.codepan.model;

import com.codepan.callback.Interface;

import java.util.ArrayList;

public class EntityData implements Interface.Searchable {

	public String ID;
	public String name;
	public String code;
	public int value;
	public boolean isActive;
	public boolean isChecked;
	public String description;
	public ArrayList<String> codeList;

	@Override
	public String name() {
		return name;
	}

	@Override
	public String code() {
		return code;
	}

	@Override
	public ArrayList<String> codeList() {
		return codeList;
	}
}
