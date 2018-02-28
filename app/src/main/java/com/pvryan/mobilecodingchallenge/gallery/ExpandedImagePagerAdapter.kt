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
package com.pvryan.mobilecodingchallenge.gallery

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.data.models.Image
import com.pvryan.mobilecodingchallenge.gallery.ExpandedImageFragment
import java.util.ArrayList

@Suppress("MemberVisibilityCanBePrivate")
// Adapter for view pager in expanded image layout
class ExpandedImagePagerAdapter(private val images: ArrayList<Image>, fm: FragmentManager) :
        FragmentStatePagerAdapter(fm) {

    // Return a new fragment for image at current position
    override fun getItem(position: Int): Fragment {
        val fragment = ExpandedImageFragment.newInstance()
        val args = Bundle()
        args.putParcelable(Constants.Keys.expandedImage, images[position])
        fragment.arguments = args
        return fragment
    }

    // Return total number of images
    override fun getCount(): Int = images.size

    // Return page title (publisher's name) for each image/page
    override fun getPageTitle(position: Int): CharSequence? {
        val image = images[position]
        return "${image.user.name} (${image.width} x ${image.height})"
    }
}
