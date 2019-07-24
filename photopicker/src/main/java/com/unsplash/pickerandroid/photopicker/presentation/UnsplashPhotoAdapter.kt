package com.unsplash.pickerandroid.photopicker.presentation

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.unsplash.pickerandroid.photopicker.R
import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto
import kotlinx.android.synthetic.main.item_unsplash_photo.view.*

/**
 * The photos recycler view adapter.
 * This is using the Android paging library to display an infinite list of photos.
 * This deals with either a single or multiple selection list.
 */
internal class UnsplashPhotoAdapter(
    private val isMultipleSelection: Boolean = false,
    private val onPhotoSelectedListener: OnPhotoSelectedListener? = null,
    private val photoSize: PhotoSize = PhotoSize.SMALL,
    private val placeHolderDrawable: Drawable? = null,
    private val errorDrawable: Drawable? = null
) : PagedListAdapter<UnsplashPhoto, UnsplashPhotoAdapter.PhotoViewHolder>(COMPARATOR) {

    // Key is index, Value is UnsplashPhoto
    private val selected = LinkedHashMap<Int, UnsplashPhoto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_unsplash_photo, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        // item
        getItem(position)?.also { photo ->
            holder.apply {
                // image
                photoImageView.aspectRatio = photo.height.toDouble() / photo.width.toDouble()
                itemView.setBackgroundColor(Color.parseColor(photo.color))
                val request = Picasso.get()
                    .load(photoSize.get(photo.urls))

                placeHolderDrawable?.also { request.placeholder(it) }
                errorDrawable?.also { request.error(it) }

                request.into(photoImageView)

                // photograph name
                nameTextView.text = photo.user.name

                // selected controls visibility

                checkedImageView.visible = adapterPosition in selected.keys
                overlay.visible = adapterPosition in selected.keys

                // click listeners
                itemView.setOnLongClickListener {
                    onPhotoSelectedListener?.onLongClickPhoto(photo, photoImageView)
                    true
                }
                itemView.setOnClickListener {
                    onPhotoSelectedListener?.onClickPhoto(photo, photoImageView)
                }
            }
        }
    }

    internal fun selectPhoto(photo: UnsplashPhoto) {
        val adapterPosition = currentList?.indexOf(photo) ?: -1
        if (!isMultipleSelection) {
            // single selection mode
            if (adapterPosition !in selected.keys) {
                selected.clear()
            }
            selected[adapterPosition] = photo
            notifyDataSetChanged()
        } else {
            // multi selection mode
            if (adapterPosition in selected.keys) {
                selected.remove(adapterPosition)
            } else {
                selected[adapterPosition] = photo
            }
            notifyItemChanged(adapterPosition)
        }
    }

    internal fun getSelectedPhotos() = selected.values.toList()

    internal fun clearSelectedPhotos() {
        selected.clear()
        notifyDataSetChanged()
    }

    companion object {
        internal val COMPARATOR = object : DiffUtil.ItemCallback<UnsplashPhoto>() {
            override fun areContentsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto) = oldItem == newItem
            override fun areItemsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto) = oldItem == newItem
        }
    }

    internal class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val photoImageView: AspectRatioImageView = view.item_unsplash_photo_image_view
        val nameTextView: TextView = view.item_unsplash_photo_text_view
        val checkedImageView: ImageView = view.item_unsplash_photo_checked_image_view
        val overlay: View = view.item_unsplash_photo_overlay
    }
}

private inline var View.visible: Boolean
    set(value) = if (value) this.visibility = View.VISIBLE else this.visibility = View.INVISIBLE
    get() = this.visibility == View.VISIBLE