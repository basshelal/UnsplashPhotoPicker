package com.unsplash.pickerandroid.photopicker.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.transition.AutoTransition
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.unsplash.pickerandroid.photopicker.R
import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto
import kotlinx.android.synthetic.main.fragment_image_show.*
import kotlinx.android.synthetic.main.photo_picker.*

class PhotoShowFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_image_show, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments!!.getParcelable<UnsplashPhoto>(PHOTO)!!.also { photo ->
            arguments!!.getString(PHOTO_SIZE)!!.also { size ->
                Picasso.get()
                    .load(PhotoSize.valueOf(size).get(photo.urls))
                    .into(image_photoView, object : Callback {
                        override fun onSuccess() {
                            image_photoView?.aspectRatio = photo.height.toDouble() / photo.width.toDouble()
                            image_photoView?.updateLayoutParams<ConstraintLayout.LayoutParams> {
                                height = WRAP_CONTENT
                            }
                            imageShow_constraintLayout?.setOnClickListener {
                                finish()
                            }
                            /* TODO update LayoutParams when any kind of zoom happens so that the zoomed image takes the whole screen */
                            image_photoView?.attacher.apply {

                                // Below dismisses fragment when fling up or down

                                /*setOnSingleFlingListener { e1, e2, velocityX, velocityY ->
                                    log("$velocityY")
                                    return@setOnSingleFlingListener if (velocityY > 7000 || velocityY < -7000) {
                                        (context as AppCompatActivity).supportFragmentManager.popBackStack()
                                        true
                                    } else false
                                }*/
                            }
                            image_progressBar?.isVisible = false
                        }

                        override fun onError(e: Exception?) {}
                    })

                user_textView?.apply {
                    text = SpannableStringBuilder(" " + photo.user.name + " ").also {
                        it.setSpan(UnderlineSpan(), 1, it.length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    setOnClickListener {
                        context.startActivity(
                            Intent().apply {
                                action = Intent.ACTION_VIEW
                                addCategory(Intent.CATEGORY_BROWSABLE)
                                data = Uri.parse("https://unsplash.com/@${photo.user.username}")
                            })
                    }
                }

                unsplash_textView?.apply {
                    text = SpannableStringBuilder(" " + text).also {
                        it.setSpan(UnderlineSpan(), 1, it.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    setOnClickListener {
                        context.startActivity(
                            Intent().apply {
                                action = Intent.ACTION_VIEW
                                addCategory(Intent.CATEGORY_BROWSABLE)
                                data = Uri.parse("https://unsplash.com/photos/${photo.id}")
                            })
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.search_cardView?.visibility = View.VISIBLE
    }

    private fun finish() {
        onBackPressed.isEnabled = false
        (activity as? AppCompatActivity?)?.supportFragmentManager?.beginTransaction()
            ?.remove(this)
            ?.commit()
    }

    companion object {
        const val TAG = "PhotoShowFragment"
        private const val PHOTO = "PHOTO"
        private const val PHOTO_SIZE = "PHOTO_SIZE"

        fun newInstance(
            photo: UnsplashPhoto,
            photoSize: PhotoSize = PhotoSize.REGULAR
        ): PhotoShowFragment {
            return PhotoShowFragment().apply {
                this.arguments = Bundle().also {
                    it.putParcelable(PHOTO, photo)
                    it.putString(PHOTO_SIZE, photoSize.name)
                }
            }
        }

        fun show(
            activity: AppCompatActivity,
            photo: UnsplashPhoto,
            photoSize: PhotoSize = PhotoSize.REGULAR
        ): PhotoShowFragment {
            activity.search_cardView?.visibility = View.INVISIBLE
            val fragment = newInstance(photo, photoSize)
            activity.supportFragmentManager
                .beginTransaction()
                .add(R.id.photoPicker_constraintLayout, fragment, TAG)
                .addToBackStack(TAG)
                .commit()
            return fragment
        }
    }
}

fun log(message: String? = "${System.currentTimeMillis()}") {
    Log.e("DEFAULT", message)
}
