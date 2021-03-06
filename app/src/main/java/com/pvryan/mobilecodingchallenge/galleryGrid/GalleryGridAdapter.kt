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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.pvryan.mobilecodingchallenge.R
import com.pvryan.mobilecodingchallenge.data.models.Image
import kotlinx.android.synthetic.main.item_image.view.ivImage

@Suppress("MemberVisibilityCanBePrivate")
// Adapter for recycler view showing images in grid layout
class GalleryGridAdapter(
        private val images: ArrayList<Image>,
        private val itemClickAction: (position: Int) -> Unit
) : RecyclerView.Adapter<GalleryGridAdapter.GalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image, parent, false)
        return GalleryViewHolder(itemView, itemClickAction)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        // Bind image at current position to view holder
        holder.bindImage(images[position], position)
    }

    // Return total number of images shown
    override fun getItemCount(): Int = images.size

    // View holder of each image
    class GalleryViewHolder(
            itemView: View,
            private val itemClickAction: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        fun bindImage(image: Image, position: Int) {
            val circularProgressDrawable = CircularProgressDrawable(itemView.context).apply {
                setStyle(CircularProgressDrawable.DEFAULT)
                strokeWidth = 2f
                centerRadius = 6f
                start()
            }
            // Load image with thumb quality
            Glide.with(itemView.context)
                    .load(Uri.parse(image.urls.thumb))
                    .apply(RequestOptions()
                            .placeholder(circularProgressDrawable)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                    )
                    .into(itemView.ivImage)
            itemView.setOnClickListener { itemClickAction(position) }
        }
    }
}
