package com.start4.tvrssreader.ui.detail

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.start4.tvrssreader.R
import com.start4.tvrssreader.ui.detail.DetailsFragment

/**
 * Details activity class that loads [DetailsFragment] class.
 */
class DetailsActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.details_fragment, DetailsFragment())
                .commitNow()
        }
    }

    companion object {
        const val SHARED_ELEMENT_NAME = "hero"
        const val MOVIE = "Movie"
        const val RSSITEM = "RSSITEM"
    }
}