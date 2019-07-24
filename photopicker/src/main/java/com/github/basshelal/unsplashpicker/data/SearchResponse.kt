package com.github.basshelal.unsplashpicker.data

internal data class SearchResponse(
    val total: Int,
    val total_pages: Int,
    val results: List<UnsplashPhoto>
)
