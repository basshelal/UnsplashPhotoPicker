package com.unsplash.pickerandroid.photopicker.customview

import android.content.Context
import android.text.InputType.*
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText

internal class EditTextView
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatEditText(context, attributeSet, defStyle) {

    var isEditable: Boolean = true
        set(value) {
            field = value
            isCursorVisible = value
            showSoftInputOnFocus = value
            isFocusableInTouchMode = value
            isFocusable = value
            isClickable = value
        }
    var isMultiLine: Boolean = false
        set(value) {
            field = value
            if (value) {
                imeOptions = EditorInfo.IME_ACTION_DONE
                setRawInputType(
                    TYPE_CLASS_TEXT or
                            TYPE_TEXT_FLAG_AUTO_CORRECT or
                            TYPE_TEXT_FLAG_CAP_SENTENCES
                )
            }
        }

    init {
        isEditable = true
    }

}