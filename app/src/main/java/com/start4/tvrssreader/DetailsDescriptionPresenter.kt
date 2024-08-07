package com.start4.tvrssreader

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import com.prof18.rssparser.model.RssItem

class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(
        viewHolder: AbstractDetailsDescriptionPresenter.ViewHolder,
        item: Any
    ) {
        val movie = item as RssItem

        viewHolder.title.text = movie.title
        viewHolder.subtitle.text = movie.image
        viewHolder.body.text = movie.description
        viewHolder.body.minHeight=1000
    }
}