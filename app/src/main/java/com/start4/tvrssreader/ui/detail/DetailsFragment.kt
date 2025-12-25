package com.start4.tvrssreader.ui.detail

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.app.DetailsSupportFragmentBackgroundController
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.OnActionClickedListener
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.prof18.rssparser.model.RssItem
import com.start4.tvrssreader.R
import com.start4.tvrssreader.ui.main.MainActivity

/**
 * A wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its metadata plus related videos.
 */
class DetailsFragment : DetailsSupportFragment() {

    private val gson = Gson()
    private var mSelectedRssItem: RssItem? = null

    private lateinit var mDetailsBackground: DetailsSupportFragmentBackgroundController
    private lateinit var mPresenterSelector: ClassPresenterSelector
    private lateinit var mAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate DetailsFragment")

        // 1️⃣ 安全获取 RSSITEM
        val json = requireActivity().intent.getStringExtra("RSSITEM")
        if (json.isNullOrEmpty()) {
            // 没传数据，直接返回 MainActivity
            startActivity(Intent(requireContext(), MainActivity::class.java))
            return
        }

        mSelectedRssItem = gson.fromJson(json, RssItem::class.java)
        if (mSelectedRssItem == null) {
            startActivity(Intent(requireContext(), MainActivity::class.java))
            return
        }

        // 2️⃣ 初始化背景
        mDetailsBackground = DetailsSupportFragmentBackgroundController(this)

        // 3️⃣ 初始化 Adapter 和 PresenterSelector
        mPresenterSelector = ClassPresenterSelector()
        mAdapter = ArrayObjectAdapter(mPresenterSelector)
        adapter = mAdapter

        // 4️⃣ 设置详情行、相关行和监听
        setupDetailsOverviewRow()
        setupDetailsOverviewRowPresenter()
        setupRelatedRssItemListRow()
        initializeBackground(mSelectedRssItem)

        onItemViewClickedListener = ItemViewClickedListener()
    }

    private fun initializeBackground(rssItem: RssItem?) {
        mDetailsBackground.enableParallax()
        Glide.with(requireContext())
            .asBitmap()
            .centerCrop()
            .error(R.drawable.default_background)
            .load(rssItem?.image)
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                    mDetailsBackground.coverBitmap = bitmap
                    mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size())
                }
            })
    }

    private fun setupDetailsOverviewRow() {
        val row = DetailsOverviewRow(mSelectedRssItem)
        row.imageDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.default_background)
        val width = convertDpToPixel(requireContext(), DETAIL_THUMB_WIDTH)
        val height = convertDpToPixel(requireContext(), DETAIL_THUMB_HEIGHT)

        Glide.with(requireContext())
            .load(mSelectedRssItem?.image)
            .centerCrop()
            .error(R.drawable.default_background)
            .into(object : SimpleTarget<Drawable>(width, height) {
                override fun onResourceReady(
                    drawable: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    row.imageDrawable = drawable
                    mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size())
                }
            })

        val actionAdapter = ArrayObjectAdapter()
        actionAdapter.add(
            Action(
                ACTION_WATCH_TRAILER,
                getString(R.string.watch_trailer_1),
                getString(R.string.watch_trailer_2)
            )
        )
        actionAdapter.add(
            Action(
                ACTION_RENT,
                getString(R.string.rent_1),
                getString(R.string.rent_2)
            )
        )
        actionAdapter.add(Action(ACTION_BUY, getString(R.string.buy_1), getString(R.string.buy_2)))

        row.actionsAdapter = actionAdapter
        mAdapter.add(row)
    }

    private fun setupDetailsOverviewRowPresenter() {
        val detailsPresenter = FullWidthDetailsOverviewRowPresenter(DetailsDescriptionPresenter())
        detailsPresenter.backgroundColor =
            ContextCompat.getColor(requireContext(), R.color.selected_background)

        val sharedElementHelper = FullWidthDetailsOverviewSharedElementHelper()
        sharedElementHelper.setSharedElementEnterTransition(
            activity,
            "hero"
        )
        detailsPresenter.setListener(sharedElementHelper)
        detailsPresenter.isParticipatingEntranceTransition = true

        detailsPresenter.onActionClickedListener = OnActionClickedListener { action ->
            // TODO: 根据 action.id 做不同处理
        }

        mPresenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
    }

    private fun setupRelatedRssItemListRow() {
        // TODO: 填充相关 RSSItem 行
    }

    private fun convertDpToPixel(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder?,
            item: Any?,
            rowViewHolder: RowPresenter.ViewHolder,
            row: Row
        ) {
            if (item is RssItem) {
                val intent = Intent(requireContext(), DetailsActivity::class.java)
                intent.putExtra("RSSITEM", gson.toJson(item))

                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    (itemViewHolder?.view as ImageCardView).mainImageView,
                    "hero"
                ).toBundle()
                startActivity(intent, bundle)
            }
        }
    }

    companion object {
        private const val TAG = "DetailsFragment"
        private const val ACTION_WATCH_TRAILER = 1L
        private const val ACTION_RENT = 2L
        private const val ACTION_BUY = 3L
        private const val DETAIL_THUMB_WIDTH = 274
        private const val DETAIL_THUMB_HEIGHT = 274
        private const val NUM_COLS = 10
    }
}
