package com.pvryan.mobilecodingchallenge

import com.pvryan.mobilecodingchallenge.data.source.ImagesRepository
import com.pvryan.mobilecodingchallenge.galleryGrid.GalleryGridViewModel
import com.pvryan.mobilecodingchallenge.galleryViewPager.GalleryViewPagerViewModel
import org.koin.androidx.viewmodel.experimental.builder.viewModel
import org.koin.dsl.module.module

val galleryDiModule = module {
    single { ImagesRepository() }
    viewModel<GalleryGridViewModel>()
    viewModel<GalleryViewPagerViewModel>()
}
