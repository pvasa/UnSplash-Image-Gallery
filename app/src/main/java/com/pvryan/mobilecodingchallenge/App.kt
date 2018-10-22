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
package com.pvryan.mobilecodingchallenge

import android.app.Application
import com.bumptech.glide.request.target.ViewTarget
import org.koin.android.ext.android.startKoin

val app get() = App.instance

class App : Application() {

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        ViewTarget.setTagId(R.id.glide_tag)

        val modules = listOf(
                galleryDiModule
        )
        // Start Koin
        startKoin(this, modules)
    }

    companion object {
        lateinit var instance: App
    }
}
