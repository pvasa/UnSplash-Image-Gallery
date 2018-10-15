package com.pvryan.mobilecodingchallenge

import com.pvryan.mobilecodingchallenge.galleryGrid.GalleryViewModel
import org.koin.androidx.viewmodel.experimental.builder.viewModel
import org.koin.dsl.module.module

val galleryDiModule = module {
    viewModel<GalleryViewModel>()
}
