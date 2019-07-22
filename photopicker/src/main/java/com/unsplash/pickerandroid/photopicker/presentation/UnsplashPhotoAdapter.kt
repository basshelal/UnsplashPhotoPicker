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

    private val selectedIndexes = ArrayList<Int>()
    private val selectedImages = ArrayList<UnsplashPhoto>()
    private var onPhotoSelectedListener: OnPhotoSelectedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_unsplash_photo, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        // item
        getItem(position)?.let { photo ->
            // image
            holder.imageView.aspectRatio = photo.height.toDouble() / photo.width.toDouble()
            holder.itemView.setBackgroundColor(Color.parseColor(photo.color))
            Picasso.get().load(photo.urls.small)
                .into(holder.imageView)
            // photograph name
            holder.txtView.text = photo.user.name
            // selected controls visibility
            holder.checkedImageView.visibility =
                if (selectedIndexes.contains(holder.adapterPosition)) View.VISIBLE else View.INVISIBLE
            holder.overlay.visibility =
                if (selectedIndexes.contains(holder.adapterPosition)) View.VISIBLE else View.INVISIBLE
            // click listener
            holder.itemView.setOnClickListener {
                // selected index(es) management
                if (selectedIndexes.contains(holder.adapterPosition)) {
                    selectedIndexes.remove(holder.adapterPosition)
                } else {
                    if (!isMultipleSelection) selectedIndexes.clear()
                    selectedIndexes.add(holder.adapterPosition)
                }
                if (isMultipleSelection) {
                    notifyDataSetChanged()
                }
                onPhotoSelectedListener?.onPhotoSelected(selectedIndexes.size)
                // change title text
            }
            holder.itemView.setOnLongClickListener {
                photo.urls.regular?.let {
                    onPhotoSelectedListener?.onPhotoLongPress(holder.imageView, it)
                }
                false
            }
        }
    }

    /**
     * Getter for the selected images.
     */
    fun getImages(): ArrayList<UnsplashPhoto> {
        selectedImages.clear()
        for (index in selectedIndexes) {
            currentList?.get(index)?.let {
                selectedImages.add(it)
            }
        }
        return selectedImages
    }

    fun clearSelection() {
        selectedImages.clear()
        selectedIndexes.clear()
    }

    fun setOnImageSelectedListener(onPhotoSelectedListener: OnPhotoSelectedListener) {
        this.onPhotoSelectedListener = onPhotoSelectedListener
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

    /**
     * UnsplashPhoto view holder.
     */
    class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: AspectRatioImageView = view.item_unsplash_photo_image_view
        val txtView: TextView = view.item_unsplash_photo_text_view
        val checkedImageView: ImageView = view.item_unsplash_photo_checked_image_view
        val overlay: View = view.item_unsplash_photo_overlay
    }
}