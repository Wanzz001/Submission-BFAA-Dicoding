package com.wanzz.githubuserapp.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wanzz.githubuserapp.R
import com.wanzz.githubuserapp.adapter.PagerAdapter
import com.wanzz.githubuserapp.database.FavoriteEntity
import com.wanzz.githubuserapp.databinding.ActivityDetailUserBinding
import com.wanzz.githubuserapp.response.DetailUserResponse
import com.wanzz.githubuserapp.viewmodel.FavoriteViewModelFactory
import com.wanzz.githubuserapp.viewmodel.UserDetailViewModel

class DetailUserActivity : AppCompatActivity() {


    private lateinit var viewModel: UserDetailViewModel

    private lateinit var binding: ActivityDetailUserBinding

    private var buttonState: Boolean = false
    private var favoriteUser: FavoriteEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = resources.getString(R.string.detail_user)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val sectionsPagerAdapter = PagerAdapter(this)
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        viewModel = obtainViewModel(this@DetailUserActivity)

        username = intent.getStringExtra(EXTRA_USER).toString()
        showViewModel()
        viewModel.getIsLoading.observe(this, this::showLoading)

    }

    private fun showViewModel() {
        viewModel.detailUser(this, username)
        viewModel.getUserDetail.observe(this) { detailUser ->

            Glide.with(this)
                .load(detailUser.avatarUrl)
                .skipMemoryCache(true)
                .into(binding.imgAvatar)

            binding.tvName.text = detailUser.name
            binding.tvUsername.text = detailUser.login
            binding.tvCompany.text = detailUser?.company ?: "-"
            binding.tvLocation.text = detailUser?.location ?: "-"
            binding.tvEmail.text = detailUser?.email ?: "-"
            binding.tvBlog.text = detailUser?.blog ?: "-"
            binding.tvRepo.text = detailUser.publicRepos.toString()
            binding.tvFollowers.text = detailUser.followers.toString()
            binding.tvFollowing.text = detailUser.following.toString()

            favoriteUser = FavoriteEntity(detailUser.id, detailUser.login)
            viewModel.getAllFavorites().observe(this) { favoriteList ->
                if (favoriteList != null) {
                    for (data in favoriteList) {
                        if (detailUser.id == data.id) {
                            buttonState = true
                            binding?.fabFavorite?.setImageResource(R.drawable.ic_favorite)
                        }
                    }
                }
            }

            binding.fabFavorite.setOnClickListener {
                if (!buttonState) {
                    buttonState = true
                    binding.fabFavorite.setImageResource(R.drawable.ic_favorite)
                    insertToDatabase(detailUser)
                } else {
                    buttonState = false
                    binding.fabFavorite.setImageResource(R.drawable.ic_unfavorite)
                    detailUser.id?.let { it1 -> viewModel.delete(it1) }
                    Toast.makeText(this, "Favorite user has been deleted.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun insertToDatabase(detailList: DetailUserResponse) {
        favoriteUser.let { favoriteUser ->
            favoriteUser?.id = detailList.id
            favoriteUser?.login = detailList.login
            favoriteUser?.avatarUrl = detailList.avatarUrl
            viewModel.insert(favoriteUser as FavoriteEntity)
            Toast.makeText(this, "User has been favorited.", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun obtainViewModel(activity: AppCompatActivity): UserDetailViewModel {
        val factory = FavoriteViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(UserDetailViewModel::class.java)
    }

    companion object {
        const val EXTRA_PREVIOUS_ACTIVITY = "extra_previous_activity"
        const val EXTRA_USER = "extra_user"
        var username = String()

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.followers,
            R.string.followings
        )
    }
}