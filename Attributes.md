# Attributes

* [Page Size](#page-size)
* [Span Count](#span-count)
* [Has Search](#has-search)
* [Persistent Search](#persistent-search#)
* [Is Multiple Selection](#is-multiple-selection)
* [Error Drawable](#error-drawable)
* [Place Holder Drawable](#place-holder-drawable)
* [Picker Photo Size](#picker-photo-size)
* [Show Photo Size](#show-photo-size)
* [Search Hint](#search-hint)
* [Photo By String](#photo-by-string)
* [On String](#on-string)
* [Click Opens Photo](#click-opens-photo)
* [Long Click Selects Photo](#long-click-selects-photo)

## Page Size

Xml: photoPicker_pageSize

Kotlin: pageSize

Type: Int

Default: 50

Defines the page size that will be used when requesting images from Unsplash, this defaults to 50.

A larger page size will mean less requests but a longer load time as each request will be larger,
a smaller page size will mean faster load times but possibly more requests depending on how many
images the user will go through.

This depends on your use case and the user's internet connection speed, the default value of 50
should be fine for most cases.

## Span Count

Xml: photoPicker_spanCount

Kotlin: spanCount

Type: Int

Default: 2

The number of columns of images that will be displayed, this defaults to 2.

It is a good idea to change this depending on orientation and/or screen size, test accordingly to find your preference.

## Has Search

Xml: photoPicker_hasSearch

Kotlin: hasSearch

Type: Boolean

Default: true

Defines whether there will be a search bar displayed at the top for the user to search Unsplash for
images, this defaults to `true`.

Generally you'll want this to be true by default and only change it when some state changes such
as when photos are selected.

You **CANNOT** use your own search View or implementation as the adapter used for the
unsplashPhotoPickerRecyclerView uses the text in the searchEditText to fetch
images from Unsplash.

## Persistent Search

Xml: photoPicker_persistentSearch

Kotlin: persistentSearch

Type: Boolean

Default: true

Defines whether the search bar will slide up when the user starts scrolling down,
this defaults to `true`.

This has no effect if hasSearch is false.

## Is Multiple Selection

Xml: photoPicker_isMultipleSelection

Kotlin: isMultipleSelection

Type: Boolean

Default: false

Defines whether selectedPhotos can contain more than one UnsplashPhoto, this defaults to `false`.

Set this to `true` if you would like the user to be able to select multiple photos.

## Error Drawable

Xml: photoPicker_errorDrawable

Kotlin: errorDrawable

Type: Drawable?

Default: null

Sets the Drawable to be used if there is an error loading an UnsplashPhoto
in unsplashPhotoPickerRecyclerView, this defaults to `null` meaning no Drawable will be used.

This just uses com.squareup.picasso.RequestCreator.error on each UnsplashPhoto
in unsplashPhotoPickerRecyclerView.

## Place Holder Drawable

Xml: photoPicker_placeHolderDrawable

Kotlin: placeHolderDrawable

Type: Drawable?

Default: null

Sets the Drawable to be used as a placeholder while loading an UnsplashPhoto in
unsplashPhotoPickerRecyclerView, this defaults to `null` meaning no Drawable will be used.

This just uses com.squareup.picasso.RequestCreator.placeholder on each UnsplashPhoto
in unsplashPhotoPickerRecyclerView.

## Picker Photo Size

Xml: photoPicker_pickerPhotoSize

Kotlin: pickerPhotoSize

Type: PhotoSize

Default: PhotoSize.SMALL

Sets the PhotoSize of each UnsplashPhoto in the unsplashPhotoPickerRecyclerView,
this defaults to PhotoSize.SMALL.

Generally speaking, you want to keep this size small, to avoid large download sizes for each image,
the default of PhotoSize.SMALL should be clear enough for most use cases.

## Show Photo Size

Xml: photoPicker_showPhotoSize

Kotlin: showPhotoSize

Type: PhotoSize

Default: PhotoSize.SMALL

Sets the PhotoSize to use when showing an UnsplashPhoto, this defaults to PhotoSize.SMALL.

This has no effect if clickOpensPhoto is false. Note that you can show UnsplashPhotos yourself by
using showPhoto.

## Search Hint

Xml: photoPicker_searchHint

Kotlin: searchHint

Type: String

Default: "Search Unsplash Photos"

Sets the hint to use on the searchEditText, this defaults to *Search Unsplash photos*.

You can change the hint either here or by changing it directly using searchEditText.

## Photo By String

Xml: photoPicker_photoByString

Kotlin: photoByString

Type: String

Default: "Photo by"  

Sets the String to be used when showing an UnsplashPhoto, this defaults to *Photo by*.

The entire text that will show when showing an UnsplashPhoto using showPhoto is:
 `Photo by *user* on Unsplash`. You can change the text for "Photo by" and "on".

Change this only for translations and internationalization (i18n). If you do change
 this, it should have no leading or trailing spaces.

## On String

Xml: photoPicker_onString

Kotlin: onString

Type: String

Default: "on"

Sets the String to be used when showing an UnsplashPhoto, this defaults to *on*.

The entire text that will show when showing an UnsplashPhoto using showPhoto is:
`Photo by *user* on Unsplash`. You can change the text for "Photo by" and "on".

Change this only for translations and internationalization (i18n). If you do change
this, it should have no leading or trailing spaces.

## Click Opens Photo

Xml: photoPicker_clickOpensPhoto

Kotlin: clickOpensPhoto

Type: Boolean

Default: true

Defines whether a click will open a photo to show, done using showPhoto, this defaults to `true`.

You can show a photo yourself using showPhoto.

## Long Click Selects Photo

Xml: photoPicker_longClickSelectsPhoto

Kotlin: longClickSelectsPhoto

Type: Boolean

Default: false

Defines whether a long click will select a photo to show, done using selectPhoto,
this defaults to `false`.

Remember to set isMultipleSelection to `true` if you want the user to be able to
select multiple photos, otherwise a long click will deselect all photos before selecting a photo.

You can select a photo yourself using selectPhoto.



