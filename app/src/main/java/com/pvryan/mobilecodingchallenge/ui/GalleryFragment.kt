/*
 * Copyright 2018 Priyank Vasa
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pvryan.mobilecodingchallenge.ui

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.R
import com.pvryan.mobilecodingchallenge.adapters.GalleryAdapter
import com.pvryan.mobilecodingchallenge.ui.extensions.getOrientation
import com.pvryan.mobilecodingchallenge.ui.extensions.isNetworkAvailable
import com.pvryan.mobilecodingchallenge.ui.extensions.snackIndefinite
import com.pvryan.mobilecodingchallenge.ui.extensions.snackLong
import kotlinx.android.synthetic.main.fragment_gallery.*

// Fragment showing images from Unsplash in a grid view
class GalleryFragment : Fragment(), LoadImagesListener {

    lateinit var mAdapter: GalleryAdapter
    private var args: Bundle? = null

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {

            if ((activity as GalleryActivity?)?.isNetworkAvailable() == false) {
                rvImages.clearOnScrollListeners()
                rvImages.snackIndefinite(Constants.Errors.noNetwork,
                        "Try again", View.OnClickListener {
                    loadRecyclerView(args)
                })
            }
        }
    }

    private val rvScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val firstPosition = (rvImages.layoutManager as GridLayoutManager)
                    .findFirstCompletelyVisibleItemPosition()
            args?.putInt(Constants.Keys.position, firstPosition)

            val lastPosition = (rvImages.layoutManager as GridLayoutManager)
                    .findLastCompletelyVisibleItemPosition()

            if (lastPosition == (mAdapter.images.size - 1)) {
                val newPage = (mAdapter.images.size / Constants.imagesPerPage) + 1
                mAdapter.loadImages(this@GalleryFragment, newPage)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.registerReceiver(
                receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onPause() {
        super.onPause()
        activity?.unregisterReceiver(receiver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let { args?.putAll(it) }
        loadRecyclerView(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Scroll to position of the last image viewed in expanded mode
        if (requestCode == Constants.Codes.expandedImageActivity
                && resultCode == Activity.RESULT_OK) {
            if (data != null && data.extras != null) {
                rvImages.scrollToPosition(
                        data.extras.getInt(Constants.Keys.position, 0))
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Current image position
        outState.putAll(args)
        super.onSaveInstanceState(outState)
    }

    // Called when there is totalImagesAvailable count fetched from Unsplash
    override fun onTotalImagesAvailable(total: Int) {
        if (mAdapter.images.size == total)
            // Remove scroll listener if all images fetched
            rvImages.clearOnScrollListeners()
    }

    // Failed to fetch images
    override fun onFailure(t: Throwable?) {
        t?.let { rvImages.snackLong(it.localizedMessage) }
    }

    private fun loadRecyclerView(savedInstanceState: Bundle? = null) {

        if ((activity as GalleryActivity?)?.isNetworkAvailable() == false) {
            rvImages.snackIndefinite(Constants.Errors.noNetwork,
                    "Try again", View.OnClickListener {
                loadRecyclerView(savedInstanceState)
            })
            return
        }

        args = Bundle()

        // Set span count according to the screen orientation
        val spanCount = if ((activity as GalleryActivity).getOrientation()
                == Configuration.ORIENTATION_LANDSCAPE) 4 else 3

        // Initialize recycler view
        rvImages.setHasFixedSize(true)
        rvImages.layoutManager = GridLayoutManager(activity, spanCount)
        rvImages.addOnScrollListener(rvScrollListener)

        rvImages.adapter = GalleryAdapter(activity as Context)
        mAdapter = rvImages.adapter as GalleryAdapter

        if (savedInstanceState == null) {
            // If new instance, retrieve new images
            mAdapter.loadImages(this)
        }
        else { // Scroll to previously visible position
            val firstPosition = savedInstanceState.getInt(Constants.Keys.position, 0)
            rvImages.scrollToPosition(firstPosition)
        }
    }

    companion object {
        fun newInstance() = GalleryFragment()
    }
}
