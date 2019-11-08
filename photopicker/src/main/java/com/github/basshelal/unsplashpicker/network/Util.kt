@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.unsplashpicker.network

import androidx.paging.DataSource

internal inline fun <K, V> DataSourceFactory(crossinline create: () -> DataSource<K, V>) =
    object : DataSource.Factory<K, V>() {
        override fun create(): DataSource<K, V> = create()
    }