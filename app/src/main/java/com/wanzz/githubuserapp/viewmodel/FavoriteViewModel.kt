package com.wanzz.githubuserapp.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.wanzz.githubuserapp.database.FavoriteEntity
import com.wanzz.githubuserapp.database.FavoriteRepository

class FavoriteViewModel(application: Application) : ViewModel() {
    private val mFavoriteRepository: FavoriteRepository = FavoriteRepository(application)
    fun getAllFavorites(): LiveData<List<FavoriteEntity>> = mFavoriteRepository.getAllFavorites()
}