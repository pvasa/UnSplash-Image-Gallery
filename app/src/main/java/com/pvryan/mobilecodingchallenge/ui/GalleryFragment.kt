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
import android.content.Intent
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
import com.pvryan.mobilecodingchallenge.utils.RetrofitHelper
import kotlinx.android.synthetic.main.fragment_gallery.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GalleryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvImages.setHasFixedSize(true)
        rvImages.layoutManager = GridLayoutManager(view.context, 3)

        RetrofitHelper.getUnsplashApi().getLatestImages(Constants.appIdUnsplash, perPage = 30)
                .enqueue(object : Callback<List<Image>> {

            override fun onFailure(call: Call<List<Image>>?, t: Throwable?) {
                t?.let { view.snackLong(it.localizedMessage) }
            }

            override fun onResponse(call: Call<List<Image>>?, response: Response<List<Image>>) {
                if (response.body() != null) {
                    rvImages.adapter = GalleryAdapter(view.context, response.body() as List<Image>)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.rcExpandedImageActivity
                && resultCode == Activity.RESULT_OK) {
            if (data != null && data.extras != null) {
                rvImages.scrollToPosition(
                        data.extras.getInt(Constants.keyPosition, 0))
            }
        }
    }

    companion object {
        fun newInstance() = GalleryFragment()
    }
}
