@file:Suppress("RedundantVisibilityModifier", "MemberVisibilityCanBePrivate", "NOTHING_TO_INLINE")

package com.github.basshelal.unsplashpicker.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.transition.AutoTransition
import com.github.basshelal.unsplashpicker.R
import com.github.basshelal.unsplashpicker.presentation.PhotoShowFragment.Companion.show
import kotlinx.android.synthetic.main.fragment_picker_show.*

/**
 * Used to show an [UnsplashPhotoPicker] on the screen quickly without having to do the layout yourself.
 *
 * The [apply] parameter will be applied to the [UnsplashPhotoPicker].
 *
 * You should use [PhotoPickerFragment.show] to quickly show an [UnsplashPhotoPicker] to the screen.
 */
class PhotoPickerFragment
@JvmOverloads constructor(private val apply: UnsplashPhotoPicker.() -> Unit = {}) : Fragment() {

    private val onBackPressed = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = AutoTransition()
        exitTransition = AutoTransition()

        (activity as? AppCompatActivity?)?.onBackPressedDispatcher
            ?.addCallback(onBackPressed)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_picker_show, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        photoPicker.apply {
            this.apply()
        }
    }

    private inline fun finish() {
        onBackPressed.isEnabled = false
        (activity as? AppCompatActivity?)?.supportFragmentManager?.beginTransaction()
            ?.remove(this)
            ?.commit()
    }

    companion object {
        const val TAG = "UnsplashPhotoPickerFragment"

        /**
         * Returns a new instance of [PhotoPickerFragment] which will show an [UnsplashPhotoPicker]
         * with the passed in [apply] block applied to it.
         *
         * You should only use this instead of [show] if you want to show the [PhotoPickerFragment]
         * yourself, meaning perform the [androidx.fragment.app.FragmentTransaction] yourself.
         * Otherwise you should use [show] instead.
         */
        public fun newInstance(apply: UnsplashPhotoPicker.() -> Unit): PhotoPickerFragment {
            return PhotoPickerFragment(apply)
        }

        /**
         * Shows an [UnsplashPhotoPicker] in a [PhotoPickerFragment] which will have the passed in
         * [apply] block applied to it and returns the [PhotoPickerFragment].
         *
         * The [container] is the [IdRes] of the container View, which will default to the main content
         *
         * This is just a wrapper around [PhotoPickerFragment.show]
         */
        public fun show(
            activity: AppCompatActivity,
            @IdRes container: Int = android.R.id.content,
            apply: UnsplashPhotoPicker.() -> Unit = {}
        ): PhotoPickerFragment {
            val fragment = newInstance(apply)
            activity.supportFragmentManager
                .beginTransaction()
                .add(container, fragment, TAG)
                .commit()
            return fragment
        }
    }
}