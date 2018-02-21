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

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.R
import com.pvryan.mobilecodingchallenge.data.Image
import kotlinx.android.synthetic.main.fragment_expanded_image.*

class ExpandedImageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_expanded_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val image = arguments?.getParcelable<Image>(Constants.keyExpandedImage)

        if (image == null) {
            view.snackLong(Constants.errorInvalidImage)
            return
        }

        Glide.with(view.context)
                .asBitmap()
                .load(Uri.parse(image.urls.full))
                .into(ivExpandedImage)

        tvName.text = image.user.name
    }

    companion object {
        fun newInstance() = ExpandedImageFragment()
    }
}
