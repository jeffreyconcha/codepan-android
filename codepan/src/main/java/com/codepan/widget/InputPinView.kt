package com.codepan.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isNotEmpty
import androidx.core.widget.doOnTextChanged
import com.codepan.R
import com.codepan.utils.CodePanUtils
import com.google.android.flexbox.FlexboxLayout

typealias OnPinComplete = (text: String) -> Unit

class InputPinView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val res = context.resources
    private val inflater = LayoutInflater.from(context)
    private var pinTextColor = res.getColor(R.color.pin_text_color)
    private var pinTextSize = res.getDimensionPixelSize(R.dimen.seventeen)
    private var itemSpacing = res.getDimensionPixelSize(R.dimen.five)
    private var itemBackground: Int
    private var pinLength = 4
    var onPinComplete: OnPinComplete? = null

    val length: Int
        get() = pinLength

    var text: String
        get() {
            var text = ""
            if (this.isNotEmpty()) {
                val layout = getChildAt(0) as FlexboxLayout
                for (index in 0 until pinLength) {
                    val item = layout.getChildAt(index) as TextView
                    text += item.text
                }
            }
            return text
        }
        set(value) {
            if (this.isNotEmpty()) {
                val layout = getChildAt(0) as FlexboxLayout
                for (index in 0 until pinLength) {
                    val item = layout.getChildAt(index) as TextView
                    if (index < value.length - 1) {
                        item.text = value[index].toString()
                    } else {
                        item.text = ""
                    }
                }
            }
        }

    val isComplete: Boolean
        get() = pinLength == text.length

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.InputPinView)
        pinLength = ta.getInteger(R.styleable.InputPinView_pinLength, pinLength)
        pinTextSize = ta.getDimensionPixelSize(R.styleable.InputPinView_pinTextSize, pinTextSize)
        pinTextColor = ta.getColor(R.styleable.InputPinView_pinTextColor, pinTextColor)
        itemSpacing = ta.getDimensionPixelSize(R.styleable.InputPinView_itemSpacing, itemSpacing)
        itemBackground =
            ta.getResourceId(R.styleable.InputPinView_itemBackground, R.drawable.state_input_pin)
        ta.recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        update()
    }

    fun update() {
        val layout = inflater.inflate(
            R.layout.input_pin_layout,
            this, false
        ) as FlexboxLayout
        for (index in 0 until pinLength) {
            val item = inflater.inflate(R.layout.input_pin_item, layout, false) as TextView
            item.also {
                val margin = (itemSpacing / 2)
                val params = item.layoutParams as FlexboxLayout.LayoutParams
                params.setMargins(margin, 0, margin, 0)
                it.layoutParams = params
                it.setTextSize(TypedValue.COMPLEX_UNIT_PX, pinTextSize.toFloat())
                it.setTextColor(pinTextColor)
                it.setBackgroundResource(itemBackground)
                it.doOnTextChanged { cs, _, _, _ ->
                    if (cs != null && cs.isNotEmpty()) {
                        it.clearFocus()
                        if (index < pinLength - 1) {
                            val next = layout.getChildAt(index + 1)
                            next.requestFocus()
                        } else {
                            CodePanUtils.hideKeyboard(it, context)
                            onPinComplete?.invoke(text)
                        }
                    }
                }
                it.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        it.text = null
                    }
                }
                it.setOnKeyListener { _, code, e ->
                    if (e.action == KeyEvent.ACTION_DOWN && code == KeyEvent.KEYCODE_DEL) {
                        if (index > 0) {
                            val prev = layout.getChildAt(index - 1)
                            prev.requestFocus()
                        }
                    }
                    false
                }
            }
            layout.addView(item)
        }
        addView(layout)
    }

    fun clear() {
        text = ""
    }
}