package com.start4.tvrssreader.ui.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.leanback.R

/**
 * ListRowHoverCardView contains a title and description.
 */
class MyCardView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    LinearLayout(context, attrs, defStyle) {
    private val mTitleView: TextView
    private val mDescriptionView: TextView

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.lb_list_row_hovercard, this)
        mTitleView = findViewById(R.id.title)
        mDescriptionView = findViewById(R.id.description)
    }

    var title: CharSequence?
        /**
         * Returns the title text.
         */
        get() = mTitleView.text
        /**
         * Sets the title text.
         */
        set(text) {
            if (!TextUtils.isEmpty(text)) {
                mTitleView.text = text
                mTitleView.visibility = VISIBLE
            } else {
                mTitleView.visibility = GONE
            }
        }

    var description: CharSequence?
        /**
         * Returns the description text.
         */
        get() = mDescriptionView.text
        /**
         * Sets the description text.
         */
        set(text) {
            if (!TextUtils.isEmpty(text)) {
                mDescriptionView.text = text
                mDescriptionView.visibility = VISIBLE
            } else {
                mDescriptionView.visibility = GONE
            }
        }
}