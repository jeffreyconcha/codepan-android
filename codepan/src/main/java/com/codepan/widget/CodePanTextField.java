package com.codepan.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.EditText;

import com.codepan.R;
import com.codepan.cache.TypefaceCache;
import com.codepan.callback.Interface.OnKeyboardDismissCallback;
import com.codepan.callback.Interface.OnTextChangedCallback;
import com.codepan.utils.CodePanUtils;
import com.codepan.utils.Debouncer;

public class CodePanTextField extends EditText implements TextWatcher {

	private boolean autoHideKeyboard, autoClearFocus, hasCloseableSpan, enableDigitSeparator;
	private OnKeyboardDismissCallback keyboardDismissCallback;
	private Drawable backgroundEnabled, backgroundDisabled;
	private OnTextChangedCallback textChangedCallback;
	private OnFocusChangeListener focusChangeListener;
	private int textColorEnabled, textColorDisabled;
	private Debouncer<String> debouncer;
	private Context context;

	public CodePanTextField(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
		this.context = context;
		addTextChangedListener(this);
	}

	public void init(final Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.codePan);
		autoHideKeyboard = ta.getBoolean(R.styleable.codePan_autoHideKeyboard, false);
		enableDigitSeparator = ta.getBoolean(R.styleable.codePan_enableDigitSeparator, false);
		autoClearFocus = ta.getBoolean(R.styleable.codePan_autoClearFocus, false);
		textColorEnabled = ta.getColor(R.styleable.codePan_textColorEnabled, getCurrentTextColor());
		textColorDisabled = ta.getColor(R.styleable.codePan_textColorDisabled, getCurrentTextColor());
		backgroundEnabled = ta.getDrawable(R.styleable.codePan_backgroundEnabled);
		backgroundDisabled = ta.getDrawable(R.styleable.codePan_backgroundDisabled);
		String typeface = ta.getString(R.styleable.codePan_typeface);
		if (typeface != null) {
			setTypeface(TypefaceCache.get(getContext().getAssets(), typeface));
		}
		setTextColor(isEnabled() ? textColorEnabled : textColorDisabled);
		setBackgroundState(backgroundEnabled);
		autoHideKeyboard(autoHideKeyboard);
		ta.recycle();
	}

	public void setBackgroundState(Drawable background) {
		if(background != null) {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				setBackground(background);
			}
			else {
				setBackgroundDrawable(background);
			}
		}
	}

	public void setParentVisibility(int visibility) {
		ViewGroup parent = (ViewGroup) this.getParent();
		parent.setVisibility(visibility);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(hasCloseableSpan) {
			if(event.getAction() == MotionEvent.ACTION_UP) {
				int x = (int) event.getX();
				int y = (int) event.getY();
				final Editable editable = getEditableText();
				VerticalImageSpan[] spans = editable.getSpans(0, editable.length(),
						VerticalImageSpan.class);
				for(VerticalImageSpan span : spans) {
					if(span.isCloseable() && span.isInsideCloseableArea(x, y)) {
						int start = span.getStart();
						int end = span.getEnd();
						editable.replace(start, end, "");
						post(() -> setSelection(editable.length()));
					}
				}
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if(enabled) {
			setTextColor(textColorEnabled);
			setBackgroundState(backgroundEnabled);
		}
		else {
			setTextColor(textColorDisabled);
			setBackgroundState(backgroundDisabled);
		}
	}

	public void autoHideKeyboard(boolean autoHideKeyboard) {
		if(autoHideKeyboard) {
			focusChangeListener = (v, hasFocus) -> {
				if(!hasFocus && (focusSearch(FOCUS_DOWN) == null ||
						(focusSearch(FOCUS_DOWN) != null && !focusSearch(FOCUS_DOWN).hasFocus()))) {
					CodePanUtils.hideKeyboard(v, context);
				}
			};
			setOnFocusChangeListener(focusChangeListener);
		}
	}

	public void setOnKeyboardDismissCallback(OnKeyboardDismissCallback keyboardDismissCallback) {
		this.keyboardDismissCallback = keyboardDismissCallback;
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_UP) {
				if (keyboardDismissCallback != null) {
					keyboardDismissCallback.onKeyboardDismiss();
				}
				if (autoClearFocus) {
					clearFocus();
				}
			}
		}
		return super.dispatchKeyEvent(event);
	}

	public void setOnTextChangedCallback(OnTextChangedCallback textChangedCallback) {
		this.textChangedCallback = textChangedCallback;
	}

	public void setOnTextChangedCallback(Debouncer<String> debouncer) {
		this.debouncer = debouncer;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence cs, int start, int lengthBefore, int lengthAfter) {
		super.onTextChanged(cs, start, lengthBefore, lengthAfter);
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (enableDigitSeparator) {
			String text = s.toString().replace(",", "");
			if (CodePanUtils.isNumeric(text)) {
				removeTextChangedListener(this);
				String formatted = CodePanUtils.groupNumbers(text);
				int cursorIndex = getSelectionEnd();
				if (cursorIndex > formatted.length()) {
					cursorIndex -= 1;
				}
				else {
					if (formatted.length() > s.length()) {
						cursorIndex += 1;
					}
				}
				setText(formatted);
				setSelection(cursorIndex);
				addTextChangedListener(this);
				triggerNotifier(text);
				return;
			}
		}
		triggerNotifier(s.toString());
	}

	public String getNumericText() {
		return getText().toString().trim().replace(",", "");
	}

	private void triggerNotifier(String text) {
		if (debouncer != null) {
			debouncer.run(text);
		}
		else {
			if (textChangedCallback != null) {
				textChangedCallback.onTextChanged(this, text);
			}
		}
	}

	public void setHasCloseableSpan(boolean hasCloseableSpan) {
		this.hasCloseableSpan = hasCloseableSpan;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (debouncer != null) {
			debouncer.cancel();
		}
	}
}
