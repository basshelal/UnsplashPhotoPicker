@file:Suppress("RedundantVisibilityModifier", "MemberVisibilityCanBePrivate", "NOTHING_TO_INLINE")

package com.github.basshelal.unsplashpicker.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.transition.AutoTransition
import com.github.basshelal.unsplashpicker.R
import com.github.basshelal.unsplashpicker.UnsplashPhotoPickerConfig
import com.github.basshelal.unsplashpicker.data.UnsplashPhoto
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_photo_show.*

/**
 * Used to show a single [UnsplashPhoto] on the screen.
 *
 * You should use [PhotoShowFragment.show] or [PhotoShowFragment.newInstance].
 */
public class PhotoShowFragment : Fragment() {

    private val onBackPressed = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() = finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = AutoTransition()
        exitTransition = AutoTransition()

        activity?.onBackPressedDispatcher?.addCallback(onBackPressed)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photo_show, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getParcelable<UnsplashPhoto>(PHOTO)?.also { photo ->
            arguments?.getString(PHOTO_SIZE)?.also { size ->
                setUpPhotoView(photo, PhotoSize.valueOf(size))
                setUpTextViews(photo)
            }
        }
    }

    private inline fun setUpPhotoView(photo: UnsplashPhoto, photoSize: PhotoSize) {
        Picasso.get()
                .load(photoSize.get(photo.urls))
                .into(image_photoView, object : Callback.EmptyCallback() {
                    override fun onSuccess() {
                        image_photoView?.apply {
                            aspectRatio = photo.height.D / photo.width.D
                            updateLayoutParams<ViewGroup.MarginLayoutParams> {
                                topMargin = if (photo.isSponsored) convertDpToPx(32, context!!) else 0
                            }
                        }
                        image_progressBar?.isVisible = false
                    }
                })
    }

    private inline fun setUpTextViews(photo: UnsplashPhoto) {
        photoBy_textView?.apply {
            text = arguments!!.getString(PHOTO_BY_STRING)
        }

        user_textView?.apply {
            text = SpannableStringBuilder(" ${photo.user.name} ").also {
                it.setSpan(UnderlineSpan(), 1, it.length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            setOnClickListener {
                goToUrl("https://unsplash.com/@${photo.user.username}")
            }
        }

        on_textView?.apply {
            text = arguments!!.getString(ON_STRING)
        }

        unsplash_textView?.apply {
            text = SpannableStringBuilder(" $text ").also {
                it.setSpan(UnderlineSpan(), 1, it.length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            setOnClickListener {
                goToUrl("https://unsplash.com/photos/${photo.id}")
            }
        }

        sponsored_linearLayout?.apply {
            isVisible = photo.isSponsored
            setOnClickListener {
                goToUrl("https://unsplash.com/brands")
            }
        }
    }

    private inline fun goToUrl(url: String) {
        context?.startActivity(
                Intent().apply {
                    action = Intent.ACTION_VIEW
                    addCategory(Intent.CATEGORY_BROWSABLE)
                    data = Uri.parse(
                            "$url?utm_source=${UnsplashPhotoPickerConfig.unsplashAppName}&utm_medium=referral"
                    )
                })
    }

    private inline fun finish() {
        onBackPressed.isEnabled = false
        activity?.supportFragmentManager?.beginTransaction()
                ?.remove(this)
                ?.commit()
    }

    override fun onDestroy() {
        super.onDestroy()

        onBackPressed.isEnabled = false
    }

    companion object {
        const val TAG = "UnsplashPhotoPickerPhotoShowFragment"
        private const val PHOTO = "PHOTO"
        private const val PHOTO_SIZE = "PHOTO_SIZE"
        private const val PHOTO_BY_STRING = "PHOTO_BY_STRING"
        private const val ON_STRING = "ON_STRING"

        /**
         * Creates a new [PhotoShowFragment] with the provided arguments.
         *
         * You should only use this instead of [show] if you want to show the [PhotoShowFragment]
         * yourself, meaning perform the [androidx.fragment.app.FragmentTransaction] yourself.
         * Otherwise you should use [show] instead.
         *
         * Shows the provided [photo] with the provided [photoSize].
         *
         * Use [photoByString] and [onString] to change the text displayed at the bottom
         * used to credit the author and Unsplash, this is only used for translation and
         * internationalization (i18n). Both strings must not contain leading or trailing spaces.
         */
        public fun newInstance(
                photo: UnsplashPhoto,
                photoSize: PhotoSize = PhotoSize.REGULAR,
                photoByString: String = "Photo by",
                onString: String = "on"
        ): PhotoShowFragment {
            return PhotoShowFragment().apply {
                this.arguments = Bundle().also {
                    it.putParcelable(PHOTO, photo)
                    it.putString(PHOTO_SIZE, photoSize.name)
                    it.putString(PHOTO_BY_STRING, photoByString)
                    it.putString(ON_STRING, onString)
                }
            }
        }

        /**
         * Shows the provided [photo] with the provided [photoSize] and returns the [PhotoShowFragment].
         *
         * You must provide the calling [FragmentActivity] which will show this [PhotoShowFragment].
         *
         * This just performs the [androidx.fragment.app.FragmentTransaction] for you, if you'd like to do so yourself,
         * you can use [newInstance].
         *
         * Use [photoByString] and [onString] to change the text displayed at the bottom
         * used to credit the author and Unsplash, this is only used for translation and
         * internationalization (i18n). Both strings must not contain leading or trailing spaces.
         */
        public fun show(
                activity: FragmentActivity,
                photo: UnsplashPhoto,
                @IdRes container: Int = android.R.id.content,
                photoSize: PhotoSize = PhotoSize.REGULAR,
                photoByString: String = "Photo by",
                onString: String = "on"
        ): PhotoShowFragment {
            val fragment = newInstance(photo, photoSize, photoByString, onString)
            activity.supportFragmentManager
                    .beginTransaction()
                    .add(container, fragment, TAG)
                    .commit()
            return fragment
        }
    }
}