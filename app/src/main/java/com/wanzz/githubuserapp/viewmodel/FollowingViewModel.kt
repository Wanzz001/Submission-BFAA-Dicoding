package com.wanzz.githubuserapp.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wanzz.githubuserapp.api.ApiConfig
import com.wanzz.githubuserapp.response.ListFollowResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowingViewModel: ViewModel() {
    private val following = MutableLiveData<ArrayList<ListFollowResponse>>()
    val getFollowing: LiveData<ArrayList<ListFollowResponse>> = following

    private val isLoading = MutableLiveData<Boolean>()
    val getIsLoading: LiveData<Boolean> = isLoading

    fun following(context: Context, username: String) {
        try {
            isLoading.value = true
            val client = ApiConfig.getApiService().following(username)
            client.enqueue(object : Callback<ArrayList<ListFollowResponse>> {
                override fun onResponse(
                    call: Call<ArrayList<ListFollowResponse>>,
                    response: Response<ArrayList<ListFollowResponse>>
                ) {
                    isLoading.value = false
                    if (response.isSuccessful && response.body() != null) {
                        following.value = response.body()
                    }
                }

                override fun onFailure(call: Call<ArrayList<ListFollowResponse>>, t: Throwable) {
                    isLoading.value = false
                    showToast(context,"Failed to fetch following. Please try again.")
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