package com.codepan.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.util.AttributeSet;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class DetailsTransition extends TransitionSet {

	public DetailsTransition() {
		init();
	}

	public DetailsTransition(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		setOrdering(ORDERING_TOGETHER);
		this.addTransition(new ChangeBounds());
		this.addTransition(new ChangeTransform());
		this.addTransition(new ChangeImageTransform());
	}
}
