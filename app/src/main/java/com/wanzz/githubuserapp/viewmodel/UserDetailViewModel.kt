package com.wanzz.githubuserapp.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wanzz.githubuserapp.api.ApiConfig
import com.wanzz.githubuserapp.database.FavoriteEntity
import com.wanzz.githubuserapp.database.FavoriteRepository
import com.wanzz.githubuserapp.response.DetailUserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val userDetail = MutableLiveData<DetailUserResponse>()
    val getUserDetail: LiveData<DetailUserResponse> = userDetail

    private val isLoading = MutableLiveData<Boolean>()
    val getIsLoading: LiveData<Boolean> = isLoading

    private val mFavoriteRepository: FavoriteRepository =
        FavoriteRepository(application)

    fun insert(user: FavoriteEntity) {
        mFavoriteRepository.insert(user)
    }

    fun delete(id: Int) {
        mFavoriteRepository.delete(id)
    }

    fun getAllFavorites(): LiveData<List<FavoriteEntity>> = mFavoriteRepository.getAllFavorites()

    fun detailUser(context: Context, username: String) {
        try {
            isLoading.value = true
            val client = ApiConfig.getApiService().detailUser(username)
            client.enqueue(object : Callback<DetailUserResponse> {
                override fun onResponse(
                    call: Call<DetailUserResponse>,
                    response: Response<DetailUserResponse>
                ) {
                    isLoading.value = false
                    if (response.isSuccessful) {
                        userDetail.value = response.body()
                    }
                }

                override fun onFailure(call: Call<DetailUserResponse>, t: Throwable) {
                    isLoading.value = false
                    showToast(context,"Failed to fetch user details. Please try again.")
                }
            })
        } catch (e: Exception) {
            showToast(context, "An unexpected error occurred. Please try again.")
        }
    }

    private fun showToast(context: Context,message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }
}