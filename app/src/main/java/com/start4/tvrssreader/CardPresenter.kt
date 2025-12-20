package com.start4.tvrssreader

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.start4.tvrssreader.data.rss.MyRssItem
import kotlin.properties.Delegates

/**
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an ImageCardView.
 */
class CardPresenter : Presenter() {
    private var mDefaultCardImage: Drawable? = null
    private var sSelectedBackgroundColor: Int by Delegates.notNull()
    private var sDefaultBackgroundColor: Int by Delegates.notNull()

    //    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
//        Log.d(TAG, "onCreateViewHolder")
//
//        sDefaultBackgroundColor = ContextCompat.getColor(parent.context, R.color.default_background)
//        sSelectedBackgroundColor =ContextCompat.getColor(parent.context, R.color.selected_background)
////        mDefaultCardImage = ContextCompat.getDrawable(parent.context, R.drawable.movie)
//
//        val cardView = object : ImageCardView(parent.context) {
//            override fun setSelected(selected: Boolean) {
//                updateCardBackgroundColor(this, selected)
//                super.setSelected(selected)
//            }
//        }
//
//        cardView.isFocusable = true
//        cardView.isFocusableInTouchMode = true
//        cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)
//
//        updateCardBackgroundColor(cardView, false)
//        return Presenter.ViewHolder(cardView)
//    }
//
//    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
//        val rssItem = item as MyRssItem
//        val cardView = viewHolder.view as ImageCardView
//
//        Log.d(TAG, "onBindViewHolder")
//        if (rssItem.title != null) {
//            cardView.titleText = rssItem.title
//            cardView.contentText = rssItem.description
//            cardView.mainImage = null
////            cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)
////            Glide.with(viewHolder.view.context)
////                .load(rssItem.image)
////                .centerCrop()
////                .error(mDefaultCardImage)
////                .into(cardView.mainImageView)
//        }
//    }
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        // 创建一个垂直布局的容器
        val container = android.widget.LinearLayout(parent.context).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(300, 200) // 设置卡片宽高
            setPadding(20, 20, 20, 20) // 设置内边距
            isFocusable = true
            isFocusableInTouchMode = true
            setBackgroundColor(Color.DKGRAY) // 默认背景色
        }

        // 标题
        val title = TextView(parent.context).apply {
            id = R.id.title_text // 建议在 ids.xml 定义
            textSize = 18f
            setTextColor(Color.WHITE)
            maxLines = 2
            ellipsize = android.text.TextUtils.TruncateAt.END
        }

        // 描述
        val content = TextView(parent.context).apply {
            id = R.id.content_text
            textSize = 14f
            setTextColor(Color.LTGRAY)
            maxLines = 3
            setPadding(0, 10, 0, 0)
        }

        container.addView(title)
        container.addView(content)

        // 处理焦点颜色变化
        container.setOnFocusChangeListener { view, hasFocus ->
            view.setBackgroundColor(if (hasFocus) Color.YELLOW else Color.DKGRAY)
            title.setTextColor(if (hasFocus) Color.BLACK else Color.WHITE)
        }

        return ViewHolder(container)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val rssItem = item as MyRssItem
        val container = viewHolder.view as ViewGroup
        val title = container.getChildAt(0) as TextView
        val content = container.getChildAt(1) as TextView

        title.text = rssItem.title
        content.text = rssItem.description
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        Log.d(TAG, "onUnbindViewHolder")
        val container = viewHolder.view as? android.view.ViewGroup
    }

    private fun updateCardBackgroundColor(view: ImageCardView, selected: Boolean) {
        val color = if (selected) sSelectedBackgroundColor else sDefaultBackgroundColor
        // Both background colors should be set because the view"s background is temporarily visible
        // during animations.
        view.setBackgroundColor(color)
        view.setInfoAreaBackgroundColor(color)
    }

    companion object {
        private val TAG = "CardPresenter"

        private val CARD_WIDTH = 313
        private val CARD_HEIGHT = 176
    }
}