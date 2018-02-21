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
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.R
import com.pvryan.mobilecodingchallenge.adapters.GalleryAdapter
import com.pvryan.mobilecodingchallenge.data.Image
import com.pvryan.mobilecodingchallenge.ui.extensions.getOrientation
import com.pvryan.mobilecodingchallenge.ui.extensions.snackLong
import com.pvryan.mobilecodingchallenge.utils.RetrofitHelper
import kotlinx.android.synthetic.main.fragment_gallery.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Fragment showing images from Unsplash in a grid view
class GalleryFragment : Fragment() {

    // List of latest images retrieved from unsplash
    var images: ArrayList<Image>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spanCount =
                if (activity?.getOrientation() == Configuration.ORIENTATION_LANDSCAPE) 4 else 3
        // Initialize recycler view
        rvImages.setHasFixedSize(false)
        rvImages.layoutManager = GridLayoutManager(view.context, spanCount)

        // If new instance, retrieve new images
        if (savedInstanceState == null) {

            // Hit Unsplash api to retrieve new images
            RetrofitHelper.getUnsplashApi().getLatestImages(Constants.appIdUnsplash, perPage = 30)
                    .enqueue(object : Callback<List<Image>> {

                        override fun onFailure(call: Call<List<Image>>?, t: Throwable?) {
                            t?.let { view.snackLong(it.localizedMessage) }
                        }

                        override fun onResponse(call: Call<List<Image>>?,
                                                response: Response<List<Image>>) {
                            if (response.body() != null) {
                                // Populate recycler view with new images
                                images = response.body() as ArrayList<Image>
                                rvImages.adapter = GalleryAdapter(view.context, ArrayList(images))
                            } else onFailure(call, Throwable(Constants.Errors.emptyBody))
                        }
                    })
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Restore image position in recycler view
        savedInstanceState?.let {
            images = savedInstanceState.getParcelableArrayList(Constants.Keys.images)
            if (rvImages.adapter == null)
                rvImages.adapter = GalleryAdapter(activity as Context, ArrayList(images))
            rvImages.scrollToPosition(savedInstanceState.getInt(Constants.Keys.position))
        }
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

        // Save image list and current image position
        if (images != null) outState.putParcelableArrayList(Constants.Keys.images, images)
        rvImages.adapter?.let {
            outState.putInt(Constants.Keys.position,
                    (it as GalleryAdapter).viewHolder.adapterPosition)
        }
        super.onSaveInstanceState(outState)
    }

    companion object {
        fun newInstance() = GalleryFragment()
    }
}
