package com.pvryan.mobilecodingchallenge.data.source

import org.koin.dsl.module.module

val imagesRepositoryDiModule = module {
    single { ImagesRepository() }
}
