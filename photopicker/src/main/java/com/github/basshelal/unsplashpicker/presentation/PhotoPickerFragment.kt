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
import kotlinx.android.synthetic.main.fragment_picker_show.*

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
            apply()
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

        public fun newInstance(apply: UnsplashPhotoPicker.() -> Unit): PhotoPickerFragment {
            return PhotoPickerFragment(apply)
        }

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