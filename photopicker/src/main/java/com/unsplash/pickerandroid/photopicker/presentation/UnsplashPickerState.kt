package com.unsplash.pickerandroid.photopicker.presentation

/**
 * UI state the picker can get itself into.
 */
enum class UnsplashPickerState {
    /** Ready to search, awaiting input */
    IDLE,
    SEARCHING,
    PHOTO_SELECTED
}
