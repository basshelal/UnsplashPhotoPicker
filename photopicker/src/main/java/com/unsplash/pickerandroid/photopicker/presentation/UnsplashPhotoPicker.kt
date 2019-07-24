package com.unsplash.pickerandroid.photopicker.presentation

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jakewharton.rxbinding2.widget.RxTextView
import com.unsplash.pickerandroid.photopicker.Injector
import com.unsplash.pickerandroid.photopicker.R
import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto
import com.unsplash.pickerandroid.photopicker.data.UnsplashUrls
import com.unsplash.pickerandroid.photopicker.domain.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.photo_picker.view.*
import java.util.concurrent.TimeUnit

public typealias OnClickPhotoCallback = (UnsplashPhoto, ImageView) -> Unit

public class UnsplashPhotoPicker
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attributeSet, defStyle) {

    //region Privates

    private val attrs = context.obtainStyledAttributes(attributeSet, R.styleable.UnsplashPhotoPicker)
    private val repository: Repository = Injector.repository
    private var adapter: UnsplashPhotoAdapter
    private var currentWatcher: TextWatcher? = null

    private inline val activity: AppCompatActivity
        get() = context as AppCompatActivity

    private var onPhotoSelectedListener = object : OnPhotoSelectedListener {
        override fun onClickPhoto(photo: UnsplashPhoto, imageView: ImageView) {
            if (clickOpensPhoto) showPhoto(photo, showPhotoSize)
            this@UnsplashPhotoPicker.onClickPhoto(photo, imageView)
        }

        override fun onLongClickPhoto(photo: UnsplashPhoto, imageView: ImageView) {
            if (longClickSelectsPhoto) selectPhoto(photo)
            this@UnsplashPhotoPicker.onLongClickPhoto(photo, imageView)
        }
    }

    //endregion Privates

    //region Public API

    // region XML attributes

    var pageSize: Int =
        attrs.getInt(R.styleable.UnsplashPhotoPicker_photoPicker_pageSize, 25)

    var spanCount: Int =
        attrs.getInt(R.styleable.UnsplashPhotoPicker_photoPicker_spanCount, 2)
        set(value) {
            field = value
            unsplashPhotoPickerRecyclerView?.layoutManager =
                StaggeredGridLayoutManager(value, StaggeredGridLayoutManager.VERTICAL)
        }

    var hasSearch: Boolean =
        attrs.getBoolean(R.styleable.UnsplashPhotoPicker_photoPicker_hasSearch, true)
        set(value) {
            field = value
            searchCardView?.isVisible = value
        }

    var persistentSearch: Boolean =
        attrs.getBoolean(R.styleable.UnsplashPhotoPicker_photoPicker_persistentSearch, false)

    var isMultipleSelection: Boolean =
        attrs.getBoolean(R.styleable.UnsplashPhotoPicker_photoPicker_isMultipleSelection, false)
        set(value) {
            field = value
            resetAdapter()
        }

    var errorDrawable: Drawable? =
        attrs.getDrawable(R.styleable.UnsplashPhotoPicker_photoPicker_errorDrawable)
        set(value) {
            field = value
            resetAdapter()
        }

    var placeHolderDrawable: Drawable? =
        attrs.getDrawable(R.styleable.UnsplashPhotoPicker_photoPicker_placeHolderDrawable)
        set(value) {
            field = value
            resetAdapter()
        }

    var pickerPhotoSize: PhotoSize =
        PhotoSize.valueOf(attrs.getInt(R.styleable.UnsplashPhotoPicker_photoPicker_pickerPhotoSize, 1))
        set(value) {
            field = value
            resetAdapter()
        }

    var showPhotoSize: PhotoSize =
        PhotoSize.valueOf(attrs.getInt(R.styleable.UnsplashPhotoPicker_photoPicker_showPhotoSize, 1))

    var searchHint: String =
        attrs.getString(R.styleable.UnsplashPhotoPicker_photoPicker_searchHint) ?: context.getString(R.string.search)
        set(value) {
            field = value
            searchEditText?.hint = value
        }

    var photoByText: String =
        attrs.getString(R.styleable.UnsplashPhotoPicker_photoPicker_photoByString)
            ?: context.getString(R.string.photoBy)

    var onText: String =
        attrs.getString(R.styleable.UnsplashPhotoPicker_photoPicker_onString) ?: context.getString(R.string.on)

    var clickOpensPhoto: Boolean =
        attrs.getBoolean(R.styleable.UnsplashPhotoPicker_photoPicker_clickOpensPhoto, true)

    var longClickSelectsPhoto: Boolean =
        attrs.getBoolean(R.styleable.UnsplashPhotoPicker_photoPicker_longClickSelectsPhoto, false)

    // endregion XML attributes

    // Views
    inline val unsplashPhotoPickerRecyclerView: RecyclerView?
        get() = unsplashPicker_recyclerView

    inline val searchCardView: CardView?
        get() = search_cardView

    inline val searchEditText: EditText?
        get() = search_editText

    // Callbacks
    var onClickPhoto: OnClickPhotoCallback = { _, _ -> }
    var onLongClickPhoto: OnClickPhotoCallback = { _, _ -> }

    // Other
    val selectedPhotos: List<UnsplashPhoto>
        get() = adapter.getSelectedPhotos()

    //endregion Public API

    init {
        require(context is AppCompatActivity) {
            "Unsplash Photo Picker Exception!\n\n" +
                    "Context Activity must subclass AppCompatActivity, provided context class is ${context.javaClass}"
        }

        View.inflate(context, R.layout.photo_picker, this)

        attrs.recycle()

        searchCardView?.isVisible = hasSearch
        searchEditText?.hint = searchHint

        val onBackPressed = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                if (this@UnsplashPhotoPicker.search_editText?.text?.isNotBlank() == true) {
                    this@UnsplashPhotoPicker.search_editText?.text = SpannableStringBuilder("")
                }
            }
        }
        activity.onBackPressedDispatcher.addCallback(onBackPressed)

        adapter = UnsplashPhotoAdapter(
            isMultipleSelection,
            onPhotoSelectedListener,
            pickerPhotoSize,
            placeHolderDrawable,
            errorDrawable
        )

        unsplashPhotoPickerRecyclerView?.apply {
            setHasFixedSize(true)
            itemAnimator = null
            layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            adapter = this@UnsplashPhotoPicker.adapter
        }

        clearSearch_imageView?.isVisible = false
        clearSearch_imageView?.setOnClickListener {
            searchEditText?.text = SpannableStringBuilder("")
        }

        searchEditText?.bindSearch()
        searchEditText?.addTextChangedListener {
            if (it != null) {
                this@UnsplashPhotoPicker.unsplashPicker_progressBar?.isVisible = true
                clearSearch_imageView?.isVisible = it.isNotBlank()
                onBackPressed.isEnabled = it.isNotBlank()
            }
        }

        unsplashPhotoPickerRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            var atTop = true
            var scrollingDown = false
            var scrollingUp = false

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (hasSearch) {
                    // Scrolling up
                    if (dy > 0 && !scrollingUp) {
                        if (!persistentSearch) {
                            searchCardView?.slideUp()
                        }
                        if (atTop) {
                            recyclerView.slideUp(200, 0F)
                            atTop = false
                        }
                        scrollingUp = true
                        scrollingDown = false
                    }
                    // Scrolling down
                    if (dy <= 0 && !scrollingDown) {
                        if (!persistentSearch) {
                            searchCardView?.slideDown()
                        }
                        scrollingDown = true
                        scrollingUp = false
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (hasSearch) {
                    val into = IntArray(spanCount) { -1 }
                    (recyclerView.layoutManager as? StaggeredGridLayoutManager)
                        ?.findFirstCompletelyVisibleItemPositions(into)
                    if (0 in into) {
                        recyclerView.slideDown(
                            150,
                            searchCardView!!.height.toFloat() + searchCardView!!.verticalMargin.toFloat()
                        )
                        atTop = true
                    }
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        searchEditText?.hideKeyboard()
                    }
                }
            }
        })

    }

    public fun clearSelectedPhotos() {
        adapter.clearSelectedPhotos()
    }

    public fun selectPhoto(photo: UnsplashPhoto) {
        adapter.selectPhoto(photo)
    }

    public fun showPhoto(
        photo: UnsplashPhoto,
        photoSize: PhotoSize = PhotoSize.REGULAR,
        photoByString: String = this.photoByText,
        onString: String = this.onText
    ): PhotoShowFragment {
        trackDownloads(listOf(photo))
        searchEditText?.hideKeyboard()
        val fragment = PhotoShowFragment.newInstance(photo, photoSize, photoByString, onString)
        activity.supportFragmentManager
            .beginTransaction()
            .add(R.id.photoPicker_constraintLayout, fragment, PhotoShowFragment.TAG)
            .commit()
        searchCardView?.visibility = View.INVISIBLE
        return fragment
    }

    /**
     * To abide by the API guidelines,
     * you need to trigger a GET request to this endpoint every time your application performs a download of a photo
     *
     * @param photos the list of selected photos
     */
    private fun trackDownloads(photos: List<UnsplashPhoto>) {
        photos.forEach { repository.trackDownload(it.links.download_location) }
    }

    private fun resetAdapter() {
        adapter = UnsplashPhotoAdapter(
            isMultipleSelection,
            onPhotoSelectedListener,
            pickerPhotoSize,
            placeHolderDrawable,
            errorDrawable
        )
    }

    @Suppress("NOTHING_TO_INLINE")
    @SuppressLint("CheckResult")
    private inline fun EditText.bindSearch() {
        RxTextView.textChanges(this)
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .observeOn(Schedulers.io())
            .switchMap { text ->
                if (TextUtils.isEmpty(text)) repository.loadPhotos(pageSize)
                else repository.searchPhotos(text.toString(), pageSize)
            }.subscribe {
                adapter.submitList(it) {
                    unsplashPhotoPickerRecyclerView?.smoothScrollToPosition(0)
                }
                if (hasSearch) {
                    unsplashPhotoPickerRecyclerView?.slideDown(
                        0,
                        searchCardView!!.height.toFloat() + searchCardView!!.verticalMargin.toFloat()
                    )
                }
                this@UnsplashPhotoPicker.unsplashPicker_progressBar?.isVisible = false
            }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun EditText.hideKeyboard() {
        if (this.hasFocus()) {
            this.clearFocus()
            this.closeKeyboard()
        }
    }
}

public enum class PhotoSize {
    THUMB, SMALL, MEDIUM, REGULAR, LARGE, FULL, RAW;

    fun get(urls: UnsplashUrls): String? {
        return when (this) {
            THUMB -> urls.thumb
            SMALL -> urls.small
            MEDIUM -> urls.medium
            REGULAR -> urls.regular
            LARGE -> urls.large
            FULL -> urls.full
            RAW -> urls.raw
        }
    }

    companion object {
        fun valueOf(ordinal: Int) = PhotoSize.values()[ordinal]
    }
}

// Private Extensions and Utilities

private inline val View.verticalMargin: Int
    get() = (layoutParams as ViewGroup.MarginLayoutParams).let { it.topMargin + it.bottomMargin }

@Suppress("NOTHING_TO_INLINE")
private inline fun View.slideUp(duration: Int = 200, amount: Float = -this.height.toFloat()) {
    ObjectAnimator.ofFloat(this, "translationY", amount - verticalMargin).apply {
        this.duration = duration.toLong()
        this.interpolator = LinearInterpolator()
    }.start()
}

@Suppress("NOTHING_TO_INLINE")
private inline fun View.slideDown(duration: Int = 200, amount: Float = 0F) {
    ObjectAnimator.ofFloat(this, "translationY", amount).apply {
        this.duration = duration.toLong()
        this.interpolator = LinearInterpolator()
    }.start()
}

private open class SimpleTextWatcher : TextWatcher {
    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    companion object {
        operator fun invoke(onChanged: (String) -> Unit) =
            object : SimpleTextWatcher() {
                override fun afterTextChanged(s: Editable?) = onChanged(s?.toString() ?: "")
            }
    }
}