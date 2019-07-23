package com.unsplash.pickerandroid.photopicker.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import com.unsplash.pickerandroid.photopicker.R
import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto
import kotlinx.android.synthetic.main.activity_image_show.*
import kotlinx.android.synthetic.main.photo_picker.*

class PhotoShowFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_image_show, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments!!.getParcelable<UnsplashPhoto>(PHOTO)!!.also { photo ->
            arguments!!.getString(PHOTO_SIZE)!!.also { size ->
                Picasso.get()
                    .load(PhotoSize.valueOf(size).get(photo.urls))
                    .into(image_photoView)
                image_photoView.aspectRatio = photo.height.toDouble() / photo.width.toDouble()
                image_photoView.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    height = WRAP_CONTENT
                }
                /* TODO update LayoutParams when any kind of zoom happens so that the zoomed image takes the whole screen */
            }
        }

        imageShow_constraintLayout.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }

        /* TODO Hyperlink the TextViews, Photo author and Unsplash website */

        // TODO swipe down should close this
    }

    override fun onDestroy() {
        super.onDestroy()

        activity?.search_cardView?.visibility = View.VISIBLE
    }

    companion object {
        const val TAG = "PhotoShowFragment"
        private const val PHOTO = "PHOTO"
        private const val PHOTO_SIZE = "PHOTO_SIZE"

        fun newInstance(
            photo: UnsplashPhoto,
            photoSize: PhotoSize = PhotoSize.SMALL
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
            photoSize: PhotoSize = PhotoSize.SMALL
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
