@file:Suppress(
        "UNUSED_ANONYMOUS_PARAMETER", "RedundantVisibilityModifier",
        "unused", "MemberVisibilityCanBePrivate", "NOTHING_TO_INLINE",
        "RemoveRedundantQualifierName"
)

package com.github.basshelal.unsplashpicker.presentation

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.annotation.IntRange
import androidx.annotation.Keep
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.core.view.updatePadding
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.basshelal.unsplashpicker.R
import com.github.basshelal.unsplashpicker.data.UnsplashPhoto
import com.github.basshelal.unsplashpicker.data.UnsplashUrls
import com.github.basshelal.unsplashpicker.network.Repository
import com.github.basshelal.unsplashpicker.network.UnsplashPhotoPickerState
import com.github.basshelal.unsplashpicker.presentation.PhotoSize.REGULAR
import com.github.basshelal.unsplashpicker.presentation.UnsplashPhotoPicker.Companion.downloadPhotos
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.photo_picker.view.*
import java.util.concurrent.TimeUnit

/**
 * Callback that is used to signify a Photo was clicked or long clicked.
 * Used in [UnsplashPhotoPicker.onClickPhoto] and [UnsplashPhotoPicker.onLongClickPhoto].
 *
 * The first parameter is the [UnsplashPhoto] that the [ImageView] is holding.
 * The second parameter is the clicked or long clicked [ImageView].
 */
public typealias OnClickPhotoCallback = (UnsplashPhoto, ImageView) -> Unit

/**
 * A complex View that displays a [RecyclerView] showing free high quality images from Unsplash.com
 * with a search bar at the top to search images from Unsplash.com.
 *
 * To quickly get started you can call [UnsplashPhotoPicker.show] to get and show an [PhotoPickerFragment]
 * or use it in your XML layouts. You can also create an [UnsplashPhotoPicker] in code by using [UnsplashPhotoPicker.get]
 *
 * Remember to call [com.github.basshelal.unsplashpicker.UnsplashPhotoPickerConfig.init] before you use this.
 * Also remember to call [downloadPhotos] when you will use the [UnsplashPhoto]s in your application in order
 * to abide by the [Unsplash API guidelines](https://help.unsplash.com/en/articles/2511245-unsplash-api-guidelines).
 *
 * For more, read the API below or on the [GitHub readme](https://github.com/basshelal/UnsplashPhotoPicker).
 */
public class UnsplashPhotoPicker
@JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyle: Int = 0
) : ConstraintLayout(context, attributeSet, defStyle) {

    //region Privates

    private val attrs =
            context.obtainStyledAttributes(attributeSet, R.styleable.UnsplashPhotoPicker)
    private var adapter: UnsplashPhotoAdapter

    private inline val activity: FragmentActivity
        get() = context as FragmentActivity

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

    // When the user has searched something, a back press will clear the selection
    // as if the search was a new fragment even though it wasn't
    private val onBackPressed = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (this@UnsplashPhotoPicker.search_editText?.text?.isNotBlank() == true) {
                clearSelectedPhotos()
                this@UnsplashPhotoPicker.search_editText?.text = SpannableStringBuilder("")
            }
        }
    }

    //endregion Privates

    //region Public API

    // region XML attributes

    /**
     * Defines the page size that will be used when requesting images from Unsplash, this defaults to 15.
     *
     * This can only be in the range of 1 to 30 inclusive if it is greater, 30 will be used.
     *
     * A larger page size will mean less requests but a longer load time as each request will be larger,
     * a smaller page size will mean faster load times but possibly more requests depending on how many
     * images the user will go through.
     *
     * This depends on your use case and the user's internet connection speed, the default value of 15
     * should be fine for most cases.
     */
    @IntRange(from = 1, to = 30)
    var pageSize: Int =
            attrs.getInt(R.styleable.UnsplashPhotoPicker_photoPicker_pageSize, 15)

    /**
     * The number of columns of images that will be displayed, this defaults to 2.
     *
     * It is a good idea to change this depending on orientation and/or screen size, test accordingly
     * to find your preference.
     */
    var spanCount: Int =
            attrs.getInt(R.styleable.UnsplashPhotoPicker_photoPicker_spanCount, 2)
        set(value) {
            field = value
            unsplashPhotoPickerRecyclerView?.layoutManager =
                    StaggeredGridLayoutManager(value, StaggeredGridLayoutManager.VERTICAL)
        }

    /**
     * Defines whether there will be a search bar displayed at the top for the user to search Unsplash for
     * images, this defaults to `true`.
     *
     * Generally you'll want this to be true by default and only change it when some state changes such
     * as when photos are selected.
     *
     * You **CANNOT** use your own search View or implementation as the adapter used for the
     * [unsplashPhotoPickerRecyclerView] uses the text in the [searchEditText] to fetch
     * images from Unsplash.
     */
    var hasSearch: Boolean =
            attrs.getBoolean(R.styleable.UnsplashPhotoPicker_photoPicker_hasSearch, true)
        set(value) {
            field = value
            searchCardView?.isVisible = value
            updatePadding()
        }

    /**
     * Defines whether the search bar will slide up when the user starts scrolling down,
     * this defaults to `true`.
     *
     * This has no effect if [hasSearch] is false.
     */
    var persistentSearch: Boolean =
            attrs.getBoolean(R.styleable.UnsplashPhotoPicker_photoPicker_persistentSearch, false)

    /**
     * Defines whether [selectedPhotos] can contain more than one [UnsplashPhoto], this defaults to `false`.
     *
     * Set this to `true` if you would like the user to be able to select multiple photos.
     */
    var isMultipleSelection: Boolean =
            attrs.getBoolean(R.styleable.UnsplashPhotoPicker_photoPicker_isMultipleSelection, false)
        set(value) {
            field = value
            adapter.isMultipleSelection = value
        }

    /**
     * Sets the [Drawable] to be used if there is an error loading an [UnsplashPhoto]
     * in [unsplashPhotoPickerRecyclerView], this defaults to `null` meaning no [Drawable] will be used.
     *
     * This just uses [com.squareup.picasso.RequestCreator.error] on each [UnsplashPhoto]
     * in [unsplashPhotoPickerRecyclerView].
     */
    var errorDrawable: Drawable? =
            attrs.getDrawable(R.styleable.UnsplashPhotoPicker_photoPicker_errorDrawable)
        set(value) {
            field = value
            adapter.errorDrawable = value
        }

    /**
     * Sets the [Drawable] to be used as a placeholder while loading an [UnsplashPhoto] in
     * [unsplashPhotoPickerRecyclerView], this defaults to `null` meaning no [Drawable] will be used.
     *
     * This just uses [com.squareup.picasso.RequestCreator.placeholder] on each [UnsplashPhoto]
     * in [unsplashPhotoPickerRecyclerView].
     */
    var placeHolderDrawable: Drawable? =
            attrs.getDrawable(R.styleable.UnsplashPhotoPicker_photoPicker_placeHolderDrawable)
        set(value) {
            field = value
            adapter.placeHolderDrawable = value
        }

    /**
     * Sets the [PhotoSize] of each [UnsplashPhoto] in the [unsplashPhotoPickerRecyclerView],
     * this defaults to [PhotoSize.SMALL].
     *
     * Generally speaking, you want to keep this size small, to avoid large download sizes for each image,
     * which would slow down each image's time to appear.
     *
     * The default of [PhotoSize.SMALL] should be clear enough for most use cases.
     */
    var pickerPhotoSize: PhotoSize =
            PhotoSize.valueOf(
                    attrs.getInt(
                            R.styleable.UnsplashPhotoPicker_photoPicker_pickerPhotoSize,
                            PhotoSize.SMALL.ordinal
                    )
            )
        set(value) {
            field = value
            adapter.photoSize = value
        }

    /**
     * Sets the [PhotoSize] to use when showing an [UnsplashPhoto] using [clickOpensPhoto],
     * this defaults to [PhotoSize.SMALL].
     *
     * This has no effect if [clickOpensPhoto] is false. Note that you can show [UnsplashPhoto]s yourself by
     * using [showPhoto].
     */
    var showPhotoSize: PhotoSize =
            PhotoSize.valueOf(
                    attrs.getInt(
                            R.styleable.UnsplashPhotoPicker_photoPicker_showPhotoSize,
                            PhotoSize.REGULAR.ordinal
                    )
            )

    /**
     * Sets the hint to use on the [searchEditText], this defaults to *Search Unsplash photos*.
     *
     * You can change the hint either here or by changing it directly using [searchEditText].
     */
    var searchHint: String =
            attrs.getString(R.styleable.UnsplashPhotoPicker_photoPicker_searchHint)
                    ?: context.getString(R.string.search)
        set(value) {
            field = value
            searchEditText?.hint = value
        }

    /**
     * Sets the String to be used when showing an [UnsplashPhoto], this defaults to *Photo by*.
     *
     * The entire text that will show when showing an [UnsplashPhoto] using [showPhoto] is:
     * `Photo by *user* on Unsplash`. You can change the text for "Photo by" and "on".
     *
     * Change this only for translations and internationalization (i18n). If you do change
     * this, it should have no leading or trailing spaces.
     */
    var photoByString: String =
            attrs.getString(R.styleable.UnsplashPhotoPicker_photoPicker_photoByString)
                    ?: context.getString(R.string.photoBy)

    /**
     * Sets the String to be used when showing an [UnsplashPhoto], this defaults to *on*.
     *
     * The entire text that will show when showing an [UnsplashPhoto] using [showPhoto] is:
     * `Photo by *user* on Unsplash`. You can change the text for "Photo by" and "on".
     *
     * Change this only for translations and internationalization (i18n). If you do change
     * this, it should have no leading or trailing spaces.
     */
    var onString: String =
            attrs.getString(R.styleable.UnsplashPhotoPicker_photoPicker_onString)
                    ?: context.getString(R.string.on)

    /**
     * Defines whether a click will open a photo to show, done using [showPhoto], this defaults to `true`.
     *
     * You can show a photo yourself using [showPhoto].
     */
    var clickOpensPhoto: Boolean =
            attrs.getBoolean(R.styleable.UnsplashPhotoPicker_photoPicker_clickOpensPhoto, true)

    /**
     * Defines whether a long click will select a photo to show, done using [selectPhoto],
     * this defaults to `false`.
     *
     * Remember to set [isMultipleSelection] to `true` if you want the user to be able to
     * select multiple photos, otherwise a long click will deselect all photos before selecting a *single* photo.
     *
     * You can select a photo yourself using [selectPhoto].
     */
    var longClickSelectsPhoto: Boolean =
            attrs.getBoolean(R.styleable.UnsplashPhotoPicker_photoPicker_longClickSelectsPhoto, false)

    // endregion XML attributes

    //region Views

    /**
     * Gets the [RecyclerView] used to display all the [UnsplashPhoto]s.
     *
     * Use this as a convenience if you want to change or access anything with the view itself.
     * Do NOT change its [RecyclerView.Adapter] as that is controlled internally.
     */
    inline val unsplashPhotoPickerRecyclerView: UnsplashPhotoPickerRecyclerView?
        get() = unsplashPicker_recyclerView

    /**
     * Gets the [CardView] used to display the search bar.
     *
     * Use this as a convenience if you want to change or access anything the with the view itself.
     */
    inline val searchCardView: CardView?
        get() = search_cardView

    /**
     * Gets the [EditText] used to to search.
     *
     * Use this as a convenience if you want to change or access anything the with the view itself.
     */
    inline val searchEditText: EditText?
        get() = search_editText

    //endregion Views

    //region Callbacks

    /**
     * The [OnClickPhotoCallback] called when an [UnsplashPhoto] in [unsplashPhotoPickerRecyclerView] is clicked.
     *
     * The first parameter is the [UnsplashPhoto] that the [ImageView] is holding.
     * The second parameter is the clicked [ImageView].
     */
    var onClickPhoto: OnClickPhotoCallback =
            { unsplashPhoto: UnsplashPhoto, imageView: ImageView -> }

    /**
     * The [OnClickPhotoCallback] called when an [UnsplashPhoto] in [unsplashPhotoPickerRecyclerView] is long clicked.
     *
     * The first parameter is the [UnsplashPhoto] that the [ImageView] is holding.
     * The second parameter is the long clicked [ImageView].
     */
    var onLongClickPhoto: OnClickPhotoCallback =
            { unsplashPhoto: UnsplashPhoto, imageView: ImageView -> }


    var onStateChanged: (UnsplashPhotoPickerState) -> Unit =
            { newState: UnsplashPhotoPickerState -> }

    //endregion Callbacks

    // Other

    /**
     * Gets the currently selected [UnsplashPhoto]s.
     *
     * You can select photos yourself using [selectPhoto]
     *
     * If [isMultipleSelection] is false, this list will never contain more than 1 item.
     */
    val selectedPhotos: List<UnsplashPhoto>
        get() = adapter.getSelectedPhotos()

    var photoPickerState: UnsplashPhotoPickerState = UnsplashPhotoPickerState.LOADING
        private set(value) {
            field = value
            onStateChanged(value)
        }

    //endregion Public API

    init {
        // The context must be a FragmentActivity because we use Fragments to show and select photos
        require(context is FragmentActivity) {
            "Unsplash Photo Picker Exception!\n\n" +
                    "Context Activity must subclass FragmentActivity, provided context class is ${context.javaClass}"
        }

        View.inflate(context, R.layout.photo_picker, this)

        // All attributes have already been used above
        attrs.recycle()

        searchCardView?.isVisible = hasSearch
        searchEditText?.hint = searchHint

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
            // never overscroll because we do overscrolling animations ourselves,
            // keeping Android overscrolls conflicts
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager =
                    StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            adapter = this@UnsplashPhotoPicker.adapter
        }

        // The clear ImageView only shows when the searchEditText isn't empty
        clearSearch_imageView?.isVisible = false
        clearSearch_imageView?.setOnClickListener {
            searchEditText?.text?.clear()
        }

        // The searchEditText does pretty much everything
        // if it's empty we get the photos Unsplash provides us asynchronously,
        // otherwise we'll search the photos with the text in the EditText
        searchEditText?.bindSearch()
        // When the text changes we also update some UI states but
        // these don't depend on whether the results have returned or not (ie,
        // they work when there is no internet)
        searchEditText?.addTextChangedListener {
            if (it != null) {
                this@UnsplashPhotoPicker.unsplashPicker_progressBar?.isVisible = true
                clearSearch_imageView?.isVisible = it.isNotBlank()
                onBackPressed.isEnabled = it.isNotBlank()
            }
        }

        // The scroll listener that deals with the sliding search bar
        unsplashPhotoPickerRecyclerView?.addOnScrollListener(
                object : RecyclerView.OnScrollListener() {

                    var scrollingDown = false
                    var scrollingUp = false

                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        if (hasSearch) {
                            // Scrolling up
                            if (dy > 0 && !scrollingUp) {
                                if (!persistentSearch) {
                                    searchCardView?.slideUp()
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
                            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                                searchEditText?.hideKeyboard()
                            }
                        }
                    }
                })
        updatePadding()

        Repository.state.observe(context, Observer {
            if (photoPickerState != it) {
                this.photoPickerState = it
                Log.e("UnsplashPhotoPicker", "state: $photoPickerState")
            }
        })
    }

    //region Public API functions

    /**
     * Clears the selected photos
     */
    public fun clearSelectedPhotos() {
        adapter.clearSelectedPhotos()
    }

    /**
     * Selects the provided [photo], remember to set [isMultipleSelection] to `true`
     * if you want the user to be able to select multiple photos,
     * otherwise this will deselect all photos before selecting a *single* photo.
     */
    public fun selectPhoto(photo: UnsplashPhoto) {
        adapter.selectPhoto(photo)
    }

    /**
     * Shows the provided [photo] with the provided [photoSize].
     *
     * Use [photoByString] and [onString] to change the text displayed at the bottom
     * used to credit the author and Unsplash, this is only used for translation and
     * internationalization (i18n). Both strings must not contain leading or trailing spaces.
     */
    public fun showPhoto(
            photo: UnsplashPhoto,
            photoSize: PhotoSize = PhotoSize.SMALL,
            photoByString: String = this.photoByString,
            onString: String = this.onString
    ): PhotoShowFragment {
        searchEditText?.hideKeyboard()
        return PhotoShowFragment.show(
                activity, photo, android.R.id.content, photoSize, photoByString, onString
        )
    }

    /**
     * For convenience and conciseness, this is just a wrapper around [kotlin.apply].
     *
     * This allows you to create code like this:
     * ```
     *      myUnsplashPhotoPicker {
     *          hasSearch = true
     *          persistentSearch = true
     *          longClickSelectsPhoto = false
     *          clickOpensPhoto = false
     *          onClick = { photo,_ -> selectPhoto(photo) }
     *      }
     * ```
     */
    public inline operator fun invoke(apply: UnsplashPhotoPicker.() -> Unit) =
            this.apply { apply() }

    companion object {

        /**
         * For convenience and conciseness this is just a wrapper around [UnsplashPhotoPicker.get]
         *
         * This allows you to create code like this in an [FragmentActivity]:
         * ```
         *      val picker = UnsplashPhotoPicker(context = this) {
         *          hasSearch = true
         *          persistentSearch = true
         *          longClickSelectsPhoto = false
         *          clickOpensPhoto = false
         *          onClick = { photo,_ -> selectPhoto(photo) }
         *      }
         * ```
         */
        public inline operator fun invoke(
                context: FragmentActivity,
                apply: UnsplashPhotoPicker.() -> Unit = {}
        ) = get(context, apply)

        /**
         * Returns an [UnsplashPhotoPicker] that with the passed in [apply] block applied to it.
         *
         * This returns an [UnsplashPhotoPicker] which will have [MATCH_PARENT] height and width.
         *
         * This is useful if you would like to add the [UnsplashPhotoPicker] yourself in code such as follows:
         *
         * ```
         *      linearLayout.addView(
         *          UnsplashPhotoPicker.get(context) {
         *              hasSearch = true
         *              persistentSearch = true
         *              longClickSelectsPhoto = false
         *              clickOpensPhoto = false
         *              onClick = { photo,_ -> selectPhoto(photo) }
         *          }
         *      )
         * ```
         */
        public inline fun get(
                context: FragmentActivity,
                apply: UnsplashPhotoPicker.() -> Unit = {}
        ): UnsplashPhotoPicker {
            return UnsplashPhotoPicker(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                        MATCH_PARENT, MATCH_PARENT
                )
                this.apply()
            }
        }

        /**
         * Shows an [UnsplashPhotoPicker] in a [PhotoPickerFragment] which will have the passed in
         * [apply] block applied to it and returns the [PhotoPickerFragment]
         *
         * This is just a wrapper around [PhotoPickerFragment.show]
         */
        public inline fun show(
                activity: FragmentActivity,
                @IdRes container: Int = android.R.id.content,
                noinline apply: UnsplashPhotoPicker.() -> Unit = {}
        ): PhotoPickerFragment =
                PhotoPickerFragment.show(activity, container, apply)

        /**
         * To abide by the
         * [API guidelines](https://help.unsplash.com/en/articles/2511245-unsplash-api-guidelines),
         * you MUST request a download to the [unsplashPhotos]s when you use them in your
         * application. This is usually when the user has picked the image(s) they want to use and you
         * will use them in your application.
         *
         * You **MUST** call this function when you will use the images to abide by the guidelines.
         *
         * @return the [unsplashPhotos] after they have sent a download request
         */
        public fun downloadPhotos(unsplashPhotos: List<UnsplashPhoto>) =
                unsplashPhotos.onEach { Repository.downloadPhoto(it.links.download_location) }

        /**
         * To abide by the
         * [API guidelines](https://help.unsplash.com/en/articles/2511245-unsplash-api-guidelines),
         * you must request a download to the [unsplashPhoto] when you use it in your
         * application. This is usually when the user has picked the image they want to use and you
         * will use it in your application.
         *
         * You **MUST** call this function when you will use the image to abide by the guidelines.
         *
         * @return the [unsplashPhoto] after it has sent a download request
         */
        public fun downloadPhoto(unsplashPhoto: UnsplashPhoto) =
                unsplashPhoto.also { Repository.downloadPhoto(it.links.download_location) }
    }

    //endregion Public API functions

    //region Private functions

    @SuppressLint("CheckResult")
    private inline fun EditText.bindSearch() {
        // The search EditText is what does everything! Any changes to it change the adapter contents
        RxTextView.textChanges(this)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .switchMap { text: CharSequence ->
                    if (text.isBlank()) Repository.loadPhotos(pageSize)
                    else Repository.searchPhotos(text.toString(), pageSize)
                }.subscribe {
                    adapter.submitList(it) {
                        if (adapter.currentList?.isNotEmpty() == true)
                            unsplashPhotoPickerRecyclerView?.scrollToPosition(0)
                    }
                    this@UnsplashPhotoPicker.unsplashPicker_progressBar?.isVisible = false
                }
    }

    private inline fun EditText.hideKeyboard() {
        if (this.hasFocus()) {
            // lose the selection so that the paste menu that appears when you long hold disappears for good
            this.setSelection(0)
            this.clearFocus()
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
                    ?.hideSoftInputFromWindow(windowToken, 0)
        }
    }

    private inline fun updatePadding() {
        // Update the padding of the recyclerView to accommodate the search bar
        postDelayed(100) {
            val padding = if (hasSearch)
                ((searchCardView?.height ?: 0) + (searchCardView?.verticalMargin ?: 0))
            else convertDpToPx(4, context)
            unsplashPhotoPickerRecyclerView?.updatePadding(top = padding)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        // If the caller destroyed this View when the search bar was full
        // we must disable the onBackPressed to avoid "empty" back presses
        onBackPressed.isEnabled = false
    }

    //endregion Private functions
}

/**
 * Enum used denote all the possible photo sizes.
 *
 * Be careful with sizes greater than [REGULAR] as they could potentially take a lot of bandwidth
 * and not fit into the ImageView depending on its size
 *
 * Read more about Photo sizes on the official
 * [Unsplash Documentation](https://unsplash.com/documentation#dynamically-resizable-images)
 */
@Keep
public enum class PhotoSize {
    THUMB, SMALL, REGULAR, FULL, RAW;

    /**
     * Gets the [String] of the Url of this [PhotoSize] from the provided [UnsplashUrls]
     */
    fun get(urls: UnsplashUrls): String? {
        return when (this) {
            THUMB -> urls.thumb
            SMALL -> urls.small
            REGULAR -> urls.regular
            FULL -> urls.full
            RAW -> urls.raw
        }
    }

    companion object {
        inline fun valueOf(ordinal: Int) = values()[ordinal]
    }
}

//region Private Extensions and Utilities

@PublishedApi
internal inline val View.verticalMargin: Int
    get() = (layoutParams as ViewGroup.MarginLayoutParams).let { it.topMargin + it.bottomMargin }

private inline fun View.slideUp(duration: Int = 200, amount: Float = -this.height.toFloat()) {
    ObjectAnimator.ofFloat(this, "translationY", amount - verticalMargin).apply {
        this.duration = duration.L
        this.interpolator = LinearInterpolator()
    }.start()
}

private inline fun View.slideDown(duration: Int = 200, amount: Float = 0F) {
    ObjectAnimator.ofFloat(this, "translationY", amount).apply {
        this.duration = duration.L
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

//endregion Private Extensions and Utilities