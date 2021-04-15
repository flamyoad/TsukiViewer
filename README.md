# Tsuki Viewer 🌙 

<span>Local doujinshi viewer for Android. </span>
<a href="https://play.google.com/store/apps/details?id=com.flamyoad.tsukiviewer" target="_blank">
 <img src="/play-store-button.png" width=200 height=60 align="right"/>
</a>

# Features

* **Organize your doujin collection**
  * Include all your doujins in one app without having them clutter your gallery app! Use the search and bookmark functions to organize your ever increasing doujins.

* **Automatic tag retrieval** 
  * ```Tsuki Viewer``` fetches english title, japanese title and tags based on the name of your directory.
  
* **Customize tags freely** 
  * Built-in editor to let you append or remove tags. You can also add your own tags on non-doujin directories.
  
* **Supports third party image-viewers** 
  * You can either use the built-in image viewer or open another gallery such as ```QuickPic``` to read your doujins.
  
* **More storage space for your phone** 
  * If you have a lot of images, most galleries build up thumbnail database which can quickly escalate to multiple GBs. If you open images from ```Tsuki Viewer```, the native gallery       app won't generate a lot of thumbnails. That means more precious storage for your phone.

# Preview

## Screenshots

![FIRST](https://user-images.githubusercontent.com/35066207/97465551-20649080-197d-11eb-9c74-d556a8af0139.JPG)

 </br>

![second](https://user-images.githubusercontent.com/35066207/97465168-bcda6300-197c-11eb-8922-d1e82347f70d.JPG)

## Chrome-style Tabbed Reader
 ```Tsuki Viewer``` allows you to quickly alternate between books without having to return to previous screen. 
 Inspired by ```Chrome``` and other modern web browsers
| Switch tabs by swiping toolbar / bottom bar                                                                           | Switch tabs from Recent Tabs screen          | 
| :-------------------------------------------------------------------------------------------------------------------: |:-------------:|
| ![swipe-demo](https://user-images.githubusercontent.com/35066207/114297060-14aaf780-9ae1-11eb-956c-1737ab90d2be.gif)  |![tab demo](https://user-images.githubusercontent.com/35066207/114297058-14126100-9ae1-11eb-8d3e-f1d0c061108d.gif) |


# Architecture
Tsuki Viewer is developed with modern Android architecture in mind and thus makes full use of [Android Jetpack](https://developer.android.com/jetpack) components such as ```ViewModel``` and ```LiveData``` to implement MVVM architecture. 

# Libraries
 List of libraries used in this project.
* [Android Jetpack](https://developer.android.com/jetpack) *(ViewModel, LiveData, Room Database, Paging Library)*
* [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
* [Retrofit](https://github.com/square/retrofit)
* [JSoup](https://github.com/jhy/jsoup)
* [Subsampling Scale Image View](https://github.com/davemorrissey/subsampling-scale-image-view)
* [Glide](https://github.com/bumptech/glide)
* [FlexboxLayout](https://github.com/google/flexbox-layout)
* [RecyclerView-FastScroll](https://github.com/timusus/RecyclerView-FastScroll)
* [ThreeTenABP](https://github.com/JakeWharton/ThreeTenABP)
* [TedPermission](https://github.com/ParkSangGwon/TedPermission)
* [Android-SpinKit](https://github.com/ybq/Android-SpinKit)
* [CircleImageView](https://github.com/hdodenhof/CircleImageView)

## License
```
Copyright 2020 Ng Zhen Hao

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
