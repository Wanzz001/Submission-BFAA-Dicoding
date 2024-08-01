package com.wanzz.githubuserapp.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wanzz.githubuserapp.api.ApiConfig
import com.wanzz.githubuserapp.response.ItemsItem
import com.wanzz.githubuserapp.response.SearchUserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchUserViewModel(context: Context) : ViewModel() {

    private val _searchList = MutableLiveData<ArrayList<ItemsItem>?>()
    val getSearchList: MutableLiveData<ArrayList<ItemsItem>?> = _searchList

    private val _isLoading = MutableLiveData<Boolean>()
    val getIsLoading: LiveData<Boolean> = _isLoading

    private var savedSearchList: ArrayList<ItemsItem>? = null

    init {
        searchUser(context, DEFAULT_SEARCH_KEYWORD)
    }

    companion object {
        private const val DEFAULT_SEARCH_KEYWORD = "www"
    }

    fun saveState() {
        savedSearchList = _searchList.value
    }

    fun restoreState() {
        _searchList.value = savedSearchList
    }

    fun searchUser(context: Context, username: String) {
        try {
            _isLoading.value = true
            val client = ApiConfig.getApiService().search(username)
            client.enqueue(object : Callback<SearchUserResponse> {
                override fun onResponse(
                    call: Call<SearchUserResponse>,
                    response: Response<SearchUserResponse>
                ) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    if (response.isSuccessful && responseBody != null) {
                        _searchList.value = ArrayList(responseBody.items)
                    } else {
                        showToast(context, "Failed to fetch search. Please try again.")
                    }
                }

                override fun onFailure(call: Call<SearchUserResponse>, t: Throwable) {
                    _isLoading.value = false
                    showToast(context, "Failed to fetch search. Please try again.")
                }
            })
        } catch (e: Exception) {
            showToast(context, "An unexpected error occurred. Please try again.")
        }
    }

    private fun showToast(context: Context, message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }
}
