@file:Suppress("unused")

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
internal object Dependencies {
    const val buildGradle = "com.android.tools.build:gradle:${Versions.buildGradle}"

    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib-jre7:${Versions.kotlin}"

    const val appcompatV7 = "com.android.support:appcompat-v7:${Versions.support}"
    const val supportDesign = "com.android.support:design:${Versions.support}"
    const val recyclerview = "com.android.support:recyclerview-v7:${Versions.support}"

    const val lifecycleExtensions = "android.arch.lifecycle:extensions:${Versions.lifecycleExtensions}"
    const val lifecycleCompiler = "android.arch.lifecycle:compiler:${Versions.lifecycleExtensions}"

    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val converterGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"

    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide}"

    const val transitionsEverywhere = "com.andkulikov:transitionseverywhere:${Versions.transitionsEverywhere}"
}

internal object TestDependencies {
    const val junit = "junit:junit:${Versions.junit}"
}

internal object AndroidTestDependencies {
    const val runner = "com.android.support.test:runner:${Versions.runner}"
    const val espresso = "com.android.support.test.espresso:espresso-core:${Versions.espresso}"
}
