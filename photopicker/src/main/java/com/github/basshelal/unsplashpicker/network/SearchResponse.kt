package com.github.basshelal.unsplashpicker.network

import com.github.basshelal.unsplashpicker.data.UnsplashPhoto

internal data class SearchResponse(
    val total: Int,
    val total_pages: Int,
    val results: List<UnsplashPhoto>
)
