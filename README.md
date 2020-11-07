# Tsuki Viewer 🌙

<span>Local doujinshi viewer for Android. </span>
<a href="https://play.google.com/store/apps/details?id=com.flamyoad.tsukiviewer">
 <img src="/play-store-button.png" width=200 height=60 align="right"/>
</a>

# Features

* **Organize your doujin collection**
  * Include all your doujins in one app without having them clutter your gallery app! Use the search and bookmark functions to organize your ever increasing doujins.

* **Automatic tag retrieval** 
  * Tsuki Viewer fetches english title, japanese title and tags from [NHentai](https://nhentai.net) based on the name of your directory.
  
* **Customize tags freely** 
  * Built-in editor to let you append or remove tags fetched from [NHentai](https://nhentai.net). You can also add your own tags on non-doujin directories.
  
* **Supports third party image-viewers** 
  * You can either use the built-in image viewer or open another gallery such as ```QuickPic``` to read your doujins.
  
* **More storage space for your phone** 
  * If you have a lot of images, most galleries build up thumbnail database which can quickly escalate to multiple GBs. If you open images from Tsuki Viewer, the native gallery       app won't generate a lot of thumbnails. That means more precious storage for your phone.

# Preview

## Screenshots

![FIRST](https://user-images.githubusercontent.com/35066207/97465551-20649080-197d-11eb-9c74-d556a8af0139.JPG)

 </br>

![second](https://user-images.githubusercontent.com/35066207/97465168-bcda6300-197c-11eb-8922-d1e82347f70d.JPG)

# Architecture
Tsuki Viewer is coded in Kotlin and makes full use of [Android Jetpack](https://developer.android.com/jetpack) components such as ```ViewModel``` and ```LiveData``` to implement MVVM architecture. 

# Libraries
 List of libraries used in this project.
* [Android Jetpack](https://developer.android.com/jetpack) *(ViewModel, LiveData, Room Database, Paging Library)*
* [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)
* [Retrofit](https://github.com/square/retrofit)
* [PhotoView](https://github.com/chrisbanes/PhotoView)
* [Glide](https://github.com/bumptech/glide)
* [FlexboxLayout](https://github.com/google/flexbox-layout)
* [RecyclerView-FastScroll](https://github.com/timusus/RecyclerView-FastScroll)
* [ThreeTenABP](https://github.com/JakeWharton/ThreeTenABP)
* [TedPermission](https://github.com/ParkSangGwon/TedPermission)
* [Android-SpinKit](https://github.com/ybq/Android-SpinKit)
* [CircleImageView](https://github.com/hdodenhof/CircleImageView)
