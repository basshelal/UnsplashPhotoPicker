package com.unsplash.pickerandroid.photopicker.customview

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jakewharton.rxbinding2.widget.RxTextView
import com.unsplash.pickerandroid.photopicker.Injector
import com.unsplash.pickerandroid.photopicker.R
import com.unsplash.pickerandroid.photopicker.UnsplashPhotoPicker
import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto
import com.unsplash.pickerandroid.photopicker.domain.Repository
import com.unsplash.pickerandroid.photopicker.presentation.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_picker.*
import kotlinx.android.synthetic.main.photo_picker.view.*
import java.util.concurrent.TimeUnit

class UnsplashPhotoPicker
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attributeSet, defStyle) {

    // API design to be changed later

    // Views
    inline val unsplashPhotoPickerRecyclerView: RecyclerView?
        get() = unsplashPicker_recyclerView

    inline val searchLayout: CardView?
        get() = search_cardView

    inline val searchTextView: EditText?
        get() = search_editText

    // XML attributes
    var spanCount: Int = 2 // xml attribute TODO, do it later!
    var allowSearch: Boolean = true // xml attribute TODO, do it later!
    var allowMultipleSelection: Boolean = false // xml attribute TODO, do it later!
    var pageSize: Int = 20 // xml attribute TODO, do it later!
    var errorImage: Drawable? = null // xml attribute TODO, do it later!
    var placeHolderImage: Drawable? = null // xml attribute TODO, do it later!

    private var currentWatcher: TextWatcher? = null

    // Callbacks
    var onSearchTextChanged: (String) -> Unit = { s -> }
        set(value) {
            search_editText.removeTextChangedListener(currentWatcher)
            field = value
            currentWatcher = SimpleTextWatcher(value)
            search_editText.addTextChangedListener(currentWatcher)
        }

    inline var onPhotoSelectedListener: OnPhotoSelectedListener?
        set(value) {
            adapter.onPhotoSelectedListener = value
        }
        get() = adapter.onPhotoSelectedListener

    /* TODO If we allow to show each image upon click on long click we must use Shared Element Transitions */

    /* TODO make the name of the author of the image be a clickable link that will send you to the image url itself*/

    val adapter: UnsplashPhotoAdapter
    val repository: Repository = Injector.repository

    // State
    private var currentState = UnsplashPickerState.IDLE
    private var previousState = UnsplashPickerState.IDLE

    init {
        View.inflate(context, R.layout.photo_picker, this)

        adapter = UnsplashPhotoAdapter(allowMultipleSelection)
        unsplashPicker_recyclerView?.apply {
            setHasFixedSize(true)
            itemAnimator = null
            layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            adapter = this@UnsplashPhotoPicker.adapter
        }

        bindSearch(search_editText)

        search_editText.setOnClickListener {
            currentState = UnsplashPickerState.SEARCHING
            updateUiFromState()
        }

        clearSearch_imageView.setOnClickListener {
            search_editText.text = SpannableStringBuilder("")
        }

        /* TODO trying to make search bar hide like Gmail */

        unsplashPicker_recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            var scrollingDown = false
            var scrollingUp = false

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // Scrolling up
                if (dy > 0 && !scrollingUp) {
                    searchLayout?.slideUp()
                    scrollingUp = true
                    scrollingDown = false
                }
                // Scrolling down
                if (dy < 0 && !scrollingDown) {
                    searchLayout?.slideDown()
                    scrollingDown = true
                    scrollingUp = false
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    this@UnsplashPhotoPicker.search_editText.also {
                        if (it.hasFocus()) {
                            it.clearFocus()
                            it.closeKeyboard()
                        }
                    }
                }
            }
        })
    }

    private fun updateUiFromState() {
        when (currentState) {
            UnsplashPickerState.IDLE -> {
            }
            UnsplashPickerState.SEARCHING -> {
            }
            UnsplashPickerState.PHOTO_SELECTED -> {
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun bindSearch(editText: EditText) {
        RxTextView.textChanges(editText)
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .observeOn(Schedulers.io())
            .switchMap { text ->
                if (TextUtils.isEmpty(text)) repository.loadPhotos(UnsplashPhotoPicker.getPageSize())
                else repository.searchPhotos(text.toString(), UnsplashPhotoPicker.getPageSize())
            }.subscribe {
                adapter.submitList(it)
            }
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
}

class UnsplashPickerActivity : AppCompatActivity(), OnPhotoSelectedListener {

    private lateinit var adapter: UnsplashPhotoAdapter

    private var isMultipleSelection = false
    private var currentState = UnsplashPickerState.IDLE
    private var previousState = UnsplashPickerState.IDLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker)

    }

    override fun onPhotosSelected(photos: List<UnsplashPhoto>) {
        val nbOfSelectedPhotos = photos.size
        // if multiple selection
        if (isMultipleSelection) {
            // update the title
            unsplash_picker_title_text_view.text = when (nbOfSelectedPhotos) {
                0 -> getString(R.string.unsplash)
                1 -> getString(R.string.photo_selected)
                else -> getString(R.string.photos_selected, nbOfSelectedPhotos)
            }
            // updating state
            if (nbOfSelectedPhotos > 0) {
                // only once, ignoring all subsequent photo selections
                if (currentState != UnsplashPickerState.PHOTO_SELECTED) {
                    previousState = currentState
                    currentState = UnsplashPickerState.PHOTO_SELECTED
                }
                updateUiFromState()
            } else { // no photo selected means un-selection
                onBackPressed()
            }
        }
        // if single selection send selected photo as a result
        else if (nbOfSelectedPhotos > 0) {
            sendPhotosAsResult()
        }
    }

    /**
     * Sends images in the result intent as a result for the calling activity.
     */
    private fun sendPhotosAsResult() {
        // get the selected photos
        val photos = adapter.getSelectedPhotos()
        // track the downloads
        //viewModel.trackDownloads(photos)
        // send them back to the calling activity
        val data = Intent()
        data.putExtra("EXTRA_PHOTOS", photos.toTypedArray())
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onPhotoLongClick(photo: UnsplashPhoto, imageView: ImageView) {
        startActivity(PhotoShowActivity.getStartingIntent(this, photo.urls.small))
    }

    override fun onBackPressed() {
        when (currentState) {
            UnsplashPickerState.IDLE -> {
                super.onBackPressed()
            }
            UnsplashPickerState.SEARCHING -> {
                // updating states
                currentState = UnsplashPickerState.IDLE
                previousState = UnsplashPickerState.SEARCHING
                // updating ui
                updateUiFromState()
            }
            UnsplashPickerState.PHOTO_SELECTED -> {
                // updating states
                currentState = if (previousState == UnsplashPickerState.SEARCHING) {
                    UnsplashPickerState.SEARCHING
                } else {
                    UnsplashPickerState.IDLE
                }
                previousState = UnsplashPickerState.PHOTO_SELECTED
                // updating ui
                updateUiFromState()
            }
        }
    }

    private fun updateUiFromState() {
        when (currentState) {
            UnsplashPickerState.IDLE -> {
                // back and search buttons visible
                unsplash_picker_back_image_view.visibility = View.VISIBLE
                unsplash_picker_search_image_view.visibility = View.VISIBLE
                // cancel and done buttons gone
                unsplash_picker_cancel_image_view.visibility = View.GONE
                unsplash_picker_done_image_view.visibility = View.GONE
                // edit text cleared and gone
                if (!TextUtils.isEmpty(unsplash_picker_edit_text.text)) {
                    unsplash_picker_edit_text.setText("")
                }
                unsplash_picker_edit_text.visibility = View.GONE
                // right clear button on top of edit text gone
                unsplash_picker_clear_image_view.visibility = View.GONE
                // keyboard down
                unsplash_picker_edit_text.closeKeyboard()
                // action bar with unsplash
                unsplash_picker_title_text_view.text = getString(R.string.unsplash)
                // clear list selection
                adapter.clearSelectedPhotos()
                adapter.notifyDataSetChanged()
            }
            UnsplashPickerState.SEARCHING -> {
                // back, cancel, done or search buttons gone
                unsplash_picker_back_image_view.visibility = View.GONE
                unsplash_picker_cancel_image_view.visibility = View.GONE
                unsplash_picker_done_image_view.visibility = View.GONE
                unsplash_picker_search_image_view.visibility = View.GONE
                // edit text visible and focused
                unsplash_picker_edit_text.visibility = View.VISIBLE
                // right clear button on top of edit text visible
                unsplash_picker_clear_image_view.visibility = View.VISIBLE
                // keyboard up
                unsplash_picker_edit_text.requestFocus()
                unsplash_picker_edit_text.openKeyboard()
                // clear list selection
                adapter.clearSelectedPhotos()
                adapter.notifyDataSetChanged()
            }
            UnsplashPickerState.PHOTO_SELECTED -> {
                // back and search buttons gone
                unsplash_picker_back_image_view.visibility = View.GONE
                unsplash_picker_search_image_view.visibility = View.GONE
                // cancel and done buttons visible
                unsplash_picker_cancel_image_view.visibility = View.VISIBLE
                unsplash_picker_done_image_view.visibility = View.VISIBLE
                // edit text gone
                unsplash_picker_edit_text.visibility = View.GONE
                // right clear button on top of edit text gone
                unsplash_picker_clear_image_view.visibility = View.GONE
                // keyboard down
                unsplash_picker_edit_text.closeKeyboard()
            }
        }
    }
}

open class SimpleTextWatcher : TextWatcher {
    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    companion object {
        operator fun invoke(onChanged: (String) -> Unit): SimpleTextWatcher {
            return object : SimpleTextWatcher() {
                override fun afterTextChanged(s: Editable?) {
                    onChanged(s?.toString() ?: "")
                }
            }
        }
    }
}

fun View.slideUp(duration: Int = 200) {
    val verticalMargin = (layoutParams as ViewGroup.MarginLayoutParams).let { it.topMargin + it.bottomMargin }
    ObjectAnimator.ofFloat(this, "translationY", -this.height.toFloat() - verticalMargin).apply {
        this.duration = duration.toLong()
        this.interpolator = AccelerateDecelerateInterpolator()
    }.start()
}

fun View.slideDown(duration: Int = 200) {
    ObjectAnimator.ofFloat(this, "translationY", 0F).apply {
        this.duration = duration.toLong()
        this.interpolator = AccelerateDecelerateInterpolator()
    }.start()
}
