package com.start4.tvrssreader.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.start4.tvrssreader.ui.list.FeedListActivity


class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, FeedListActivity::class.java))
        finish()
    }

}


/**
 * Loads [MainFragment].
 */
//class MainActivity : FragmentActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.main_browse_fragment, MainFragment())
//                .commitNow()
//        }
//    }
//}

