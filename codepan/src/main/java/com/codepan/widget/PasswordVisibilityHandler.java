package com.codepan.widget;

import android.content.Context;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.codepan.R;
public class PasswordVisibilityHandler extends FrameLayout {

	private boolean isPasswordVisible;
	private LayoutInflater inflater;

	public PasswordVisibilityHandler(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflater = LayoutInflater.from(context);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		final View view = inflater.inflate(R.layout.password_visibility_handler_layout, this, false);
		final CheckBox cbIconPasswordVisibilityHandler = view.findViewById(R.id.cbIconPasswordVisibilityHandler);
		view.setOnClickListener(v -> {
			isPasswordVisible = !isPasswordVisible;
			ViewGroup parent = (ViewGroup) PasswordVisibilityHandler.this.getParent();
			final int count = parent.getChildCount();
			for(int i = 0; i < count; i++) {
				View child = parent.getChildAt(i);
				if(child instanceof EditText) {
					EditText field = (EditText) child;
					TransformationMethod transformation = isPasswordVisible ? null : new PasswordTransformationMethod();
					field.setTransformationMethod(transformation);
					String text = field.getText().toString();
					field.setSelection(text.length());
				}
				cbIconPasswordVisibilityHandler.setChecked(isPasswordVisible);
			}
		});
		addView(view);
	}
}
