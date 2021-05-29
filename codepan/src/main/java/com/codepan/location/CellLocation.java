package com.codepan.location;

import android.content.Context;
import android.location.Location;

import com.codepan.model.PhoneInfoData;
import com.codepan.utils.CodePanUtils;

public class CellLocation extends Location {

	private PhoneInfoData info;

	public CellLocation(String provider) {
		super(provider);
	}

	public void updatePhoneInfo(Context context) {
		this.info = CodePanUtils.getPhoneInfo(context);
	}

	public PhoneInfoData getPhoneInfo() {
		return info;
	}

	public boolean compareInfo(PhoneInfoData input) {
		if(info != null && input != null) {
			return info.cid == input.cid && info.lac == input.lac;
		}
		return false;
	}
}
