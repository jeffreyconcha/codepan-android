package com.codepan.cache;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.Hashtable;

public class TypefaceCache {

	private static final Hashtable<String, Typeface> CACHE = new Hashtable<>();

	public static Typeface get(AssetManager manager, String name) {
		synchronized(CACHE) {
			if(!CACHE.containsKey(name)) {
				Typeface t = Typeface.createFromAsset(manager, name);
				CACHE.put(name, t);
			}
			return CACHE.get(name);
		}
	}

	public static Typeface get(Context context, String name) {
		synchronized(CACHE) {
			if(!CACHE.containsKey(name)) {
				AssetManager manager = context.getAssets();
				Typeface t = Typeface.createFromAsset(manager, name);
				CACHE.put(name, t);
			}
			return CACHE.get(name);
		}
	}
}
