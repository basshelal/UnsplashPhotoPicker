package com.unsplash.pickerandroid.photopicker.presentation

import android.graphics.Color
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
open class UnsplashPhotoAdapter constructor(private val isMultipleSelection: Boolean) :
    PagedListAdapter<UnsplashPhoto, UnsplashPhotoAdapter.PhotoViewHolder>(COMPARATOR) {

    // Key is index, Value is UnsplashPhoto
    private val selected = LinkedHashMap<Int, UnsplashPhoto>()
    var onPhotoSelectedListener: OnPhotoSelectedListener? = null

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
                Picasso.get().load(photo.urls.small)
                    .into(photoImageView)

                // photograph name
                nameTextView.text = photo.user.name

                // selected controls visibility
                checkedImageView.visibility =
                    if (selected.keys.contains(adapterPosition)) View.VISIBLE else View.INVISIBLE
                overlay.visibility =
                    if (selected.keys.contains(adapterPosition)) View.VISIBLE else View.INVISIBLE

                // click listener
                itemView.setOnClickListener {
                    // selected index(es) management
                    if (adapterPosition in selected.keys) {
                        selected.remove(adapterPosition)
                    } else {
                        if (!isMultipleSelection) selected.clear()
                        selected[adapterPosition] = photo
                    }
                    if (isMultipleSelection) {
                        notifyDataSetChanged()
                    }
                    onPhotoSelectedListener?.onPhotosSelected(selected.values.toList())
                    // change title text
                    holder.itemView.setOnLongClickListener {
                        onPhotoSelectedListener?.onPhotoLongClick(photo, photoImageView)
                        false
                    }
                }
            }
        }
    }

    fun getSelectedPhotos(): List<UnsplashPhoto> {
        return selected.values.toList()
    }

    fun clearSelectedPhotos() {
        selected.clear()
    }

    companion object {
        // diff util comparator
        val COMPARATOR = object : DiffUtil.ItemCallback<UnsplashPhoto>() {
            override fun areContentsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto): Boolean =
                oldItem == newItem
        }
    }

    class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val photoImageView: AspectRatioImageView = view.item_unsplash_photo_image_view
        val nameTextView: TextView = view.item_unsplash_photo_text_view
        val checkedImageView: ImageView = view.item_unsplash_photo_checked_image_view
        val overlay: View = view.item_unsplash_photo_overlay
    }
}