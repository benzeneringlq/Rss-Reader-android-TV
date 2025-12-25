package com.start4.tvrssreader.ui.main

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.OnItemViewSelectedListener
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.start4.tvrssreader.BrowseErrorActivity
import com.start4.tvrssreader.CardPresenter
import com.start4.tvrssreader.R
import com.start4.tvrssreader.data.rss.MyRssItem
import com.start4.tvrssreader.ui.detail.DetailsActivity
import com.start4.tvrssreader.ui.setting.SettingsActivity
import java.util.Timer
import java.util.TimerTask

class MainFragment : BrowseSupportFragment() {

    private val gson = Gson()
    private val channelCache = mutableMapOf<Long, String>()
    private lateinit var viewModel: MyViewModel
    private lateinit var rowsAdapter: ArrayObjectAdapter
    private val mHandler = Handler(Looper.myLooper()!!)

    private lateinit var mBackgroundManager: BackgroundManager
    private var mDefaultBackground: Drawable? = null
    private lateinit var mMetrics: DisplayMetrics
    private var mBackgroundTimer: Timer? = null
    private var mBackgroundUri: String? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        prepareBackgroundManager()
        setupUIElements()
        setupAdapters()
        setupEventListeners()
        prepareViewModel()
    }

    private fun prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(requireActivity())
        mBackgroundManager.attach(requireActivity().window)
        mDefaultBackground =
            ContextCompat.getDrawable(requireContext(), R.drawable.default_background)
        mMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(mMetrics)
    }

    private fun setupUIElements() {
        title = getString(R.string.browse_title)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(requireContext(), R.color.fastlane_background)
        searchAffordanceColor = ContextCompat.getColor(requireContext(), R.color.search_opaque)
    }

    private fun setupAdapters() {
        // 使用 ListRowPresenter 来展示每一行
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        adapter = rowsAdapter
    }

    private fun prepareViewModel() {
        viewModel = ViewModelProvider(requireActivity())[MyViewModel::class.java]
        Log.d("channel", "频道数量: ${viewModel.channels.value?.size ?: 0}")
        viewModel.channels.observe(viewLifecycleOwner) { channels ->
            channelCache.clear()
            channels.forEach { channel ->
                channelCache[channel.channelId] =
                    channel.title ?: "频道数量: ${viewModel.channels.value?.size ?: 0}"
                Log.d("MainFragment", "缓存频道: ${channel.channelId} -> ${channel.title}")
            }
        }
        // 获取数据
        viewModel.refreshRssData()

        // 观察数据
        viewModel.rssItems.observe(viewLifecycleOwner) { rssItems ->
            updateRows(rssItems)
        }
    }

    /**
     * 根据频道 ID 获取频道名称
     */
    private fun getChannelNameById(channelId: Long): String {
        return channelCache[channelId] ?: "频道数量: ${viewModel.channels.value?.size ?: 0}"
    }

    private fun updateRows(rssItems: List<MyRssItem>) {
        Log.d("MainFragment", "收到数据长度: ${rssItems.size}")
        rowsAdapter.clear()

        // 1. 按频道分组展示 RSS 内容
        // 假设 MyRssItem 有 channelName 字段，如果没有，可以根据 channelId 分组
        val groupedItems = rssItems.groupBy { it.channelId ?: 0L }

        groupedItems.forEach { (channelId, items) ->
            val listRowAdapter = ArrayObjectAdapter(CardPresenter())
            items.forEach { listRowAdapter.add(it) }
            val channelName = getChannelNameById(channelId)
            val header = HeaderItem(channelId as Long, channelName ?: "Feed")
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }

        // 2. 添加底部的设置栏
        val gridHeader = HeaderItem(NUM_ROWS.toLong(), "PREFERENCES")
        val gridRowAdapter = ArrayObjectAdapter(GridItemPresenter())
        gridRowAdapter.add(getString(R.string.grid_view))
        gridRowAdapter.add(getString(R.string.error_fragment))
        gridRowAdapter.add(getString(R.string.personal_settings))
        rowsAdapter.add(ListRow(gridHeader, gridRowAdapter))
    }

    private fun setupEventListeners() {
        setOnSearchClickedListener {
            Toast.makeText(requireContext(), "Search not implemented", Toast.LENGTH_SHORT).show()
        }

        onItemViewClickedListener = ItemViewClickedListener()
        onItemViewSelectedListener = ItemViewSelectedListener()
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder,
            item: Any,
            rowViewHolder: RowPresenter.ViewHolder,
            row: Row
        ) {
            // 修正：这里应该判断 MyRssItem，因为你的 Adapter 里放的是它
            if (item is MyRssItem) {
                val intent = Intent(requireContext(), DetailsActivity::class.java)
                intent.putExtra("RSSITEM", gson.toJson(item))

                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    (itemViewHolder.view as ImageCardView).mainImageView,
                    "hero"
                ).toBundle()
                startActivity(intent, bundle)

            } else if (item is String) {
                when {
                    item.contains(getString(R.string.error_fragment)) -> {
                        startActivity(Intent(requireContext(), BrowseErrorActivity::class.java))
                    }

                    item.contains(getString(R.string.personal_settings)) -> {
                        startActivity(Intent(requireContext(), SettingsActivity::class.java))
                    }

                    else -> Toast.makeText(requireContext(), item, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(
            itemViewHolder: Presenter.ViewHolder?, item: Any?,
            rowViewHolder: RowPresenter.ViewHolder, row: Row
        ) {
            if (item is MyRssItem) {
                // 如果 RSS 条目有图片，更新背景
                // mBackgroundUri = item.imageUrl
                // startBackgroundTimer()
            }
        }
    }

    // 更新背景的具体实现（修复了 SimpleTarget 过时的问题）
    private fun updateBackground(uri: String?) {
        Glide.with(requireContext())
            .load(uri)
            .centerCrop()
            .error(mDefaultBackground)
            .into(object : CustomTarget<Drawable>(mMetrics.widthPixels, mMetrics.heightPixels) {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    mBackgroundManager.drawable = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun startBackgroundTimer() {
        mBackgroundTimer?.cancel()
        mBackgroundTimer = Timer()
        mBackgroundTimer?.schedule(object : TimerTask() {
            override fun run() {
                mHandler.post { updateBackground(mBackgroundUri) }
            }
        }, BACKGROUND_UPDATE_DELAY.toLong())
    }

    override fun onDestroy() {
        super.onDestroy()
        mBackgroundTimer?.cancel()
    }

    // 内部类：网格设置项的样式
    private inner class GridItemPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val view = TextView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT)
                isFocusable = true
                isFocusableInTouchMode = true
                setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.default_background
                    )
                )
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
            }
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            (viewHolder.view as TextView).text = item as String
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
    }

    companion object {
        private const val BACKGROUND_UPDATE_DELAY = 300
        private const val GRID_ITEM_WIDTH = 200
        private const val GRID_ITEM_HEIGHT = 200
        private const val NUM_ROWS = 6
    }
}