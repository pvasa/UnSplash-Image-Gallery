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
package com.pvryan.mobilecodingchallenge.data.source.local

import com.pvryan.mobilecodingchallenge.Constants
import com.pvryan.mobilecodingchallenge.data.models.Image
import com.pvryan.mobilecodingchallenge.data.source.ImagesDataSource
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import okhttp3.Headers
import retrofit2.Response

class ImagesLocalDataSource : ImagesDataSource {

    private val images = ArrayList<Image>()

    override fun saveImages(images: ArrayList<Image>) {
        this.images.addAll(images)
    }

    override fun loadImages(
            page: Int,
            imagesPerPage: Int
    ): Deferred<Response<List<Image>>> = GlobalScope.async {

        /*val (firstIndex, lastIndex) = when {

            page <= images.size / imagesPerPage ->
                Pair((page * imagesPerPage) - imagesPerPage - 1, (page * imagesPerPage) - 1)

            else -> Pair(0, -1)
        }

        val requestedImages: List<Image> =
                if (firstIndex > lastIndex) emptyList()
                else ArrayList(images.subList(firstIndex, lastIndex + 1))

        val headerXTotal = Pair(
                Constants.Headers.xTotal,
                (if (requestedImages.isNotEmpty()) images.size else 0).toString()
        )

        Response.success(requestedImages, Headers.of(mapOf(headerXTotal)))*/
        Response.success(emptyList<Image>(), Headers.of(mapOf(Pair(Constants.Headers.xTotal, 0.toString()))))
    }
}
