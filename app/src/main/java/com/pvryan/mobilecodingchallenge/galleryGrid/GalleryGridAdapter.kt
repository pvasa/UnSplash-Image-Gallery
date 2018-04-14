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
package com.pvryan.mobilecodingchallenge.galleryGrid

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.pvryan.mobilecodingchallenge.R
import com.pvryan.mobilecodingchallenge.data.models.Image
import kotlinx.android.synthetic.main.item_image.view.*

@Suppress("MemberVisibilityCanBePrivate")
// Adapter for recycler view showing images in grid layout
class GalleryGridAdapter(private val images: ArrayList<Image>,
                         private val userActionListener: ImageItemUserActionListener) :
        RecyclerView.Adapter<GalleryGridAdapter.GalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image, parent, false)
        return GalleryViewHolder(itemView, userActionListener)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        // Bind image at current position to view holder
        holder.bindImage(images[position], position)
    }

    // Return total number of images shown
    override fun getItemCount(): Int = images.size

    // View holder of each image
    class GalleryViewHolder(itemView: View,
                            private val userActionListener: ImageItemUserActionListener) :
            RecyclerView.ViewHolder(itemView) {

        fun bindImage(image: Image, position: Int) {
            // Load image with thumb quality
            Glide.with(itemView.context)
                    .asBitmap()
                    .load(Uri.parse(image.urls.thumb))
                    .into(itemView.ivImage)
            itemView.setOnClickListener { userActionListener.onImageClick(position) }
        }
    }
}
