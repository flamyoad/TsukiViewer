package com.flamyoad.tsukiviewer.ui.home.tags

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.TagDao
import com.flamyoad.tsukiviewer.model.Tag
import com.flamyoad.tsukiviewer.model.TagType

class DoujinTagsViewModel(application: Application) : AndroidViewModel(application) {

    private val db: AppDatabase
    private val tagDao: TagDao

    private val allTags: LiveData<List<Tag>>
    private val parodies: LiveData<List<Tag>>
    private val characters: LiveData<List<Tag>>
    private val tags: LiveData<List<Tag>>
    private val artists: LiveData<List<Tag>>
    private val groups: LiveData<List<Tag>>
    private val languages: LiveData<List<Tag>>
    private val categories: LiveData<List<Tag>>

    init {
        db = AppDatabase.getInstance(application)
        tagDao = db.tagsDao()

        allTags = tagDao.getAll()

        parodies = tagDao.getByCategory(TagType.Parodies.getLowerCaseName())
        characters = tagDao.getByCategory(TagType.Characters.getLowerCaseName())
        tags = tagDao.getByCategory(TagType.Tags.getLowerCaseName())
        artists = tagDao.getByCategory(TagType.Artists.getLowerCaseName())
        groups = tagDao.getByCategory(TagType.Groups.getLowerCaseName())
        languages = tagDao.getByCategory(TagType.Languages.getLowerCaseName())
        categories = tagDao.getByCategory(TagType.Categories.getLowerCaseName())
    }

    fun getTagItems(type: TagType): LiveData<List<Tag>> {
        return when(type) {
            TagType.All -> allTags
            TagType.Parodies -> parodies
            TagType.Characters -> characters
            TagType.Tags -> tags
            TagType.Artists -> artists
            TagType.Groups -> groups
            TagType.Languages -> languages
            TagType.Categories -> categories
        }
    }
}