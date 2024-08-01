package com.wanzz.githubuserapp.ui

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.wanzz.githubuserapp.adapter.FavoriteAdapter
import com.wanzz.githubuserapp.database.FavoriteEntity
import com.wanzz.githubuserapp.databinding.ActivityFavoriteBinding
import com.wanzz.githubuserapp.viewmodel.FavoriteViewModel
import com.wanzz.githubuserapp.viewmodel.FavoriteViewModelFactory

class FavoriteActivity : AppCompatActivity() {
    private val adapter = FavoriteAdapter()

    private lateinit var binding: ActivityFavoriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Favorite Users"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        showRecyclerView()
    }

    private fun showRecyclerView() {
        val favoriteViewModel = obtainViewModel(this@FavoriteActivity)
        favoriteViewModel.getAllFavorites().observe(this) { favoriteList ->
            if (favoriteList != null) {
                adapter.setData(favoriteList)
            }
        }
        binding.rvUser.layoutManager =
            if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager(this, 2)
            } else {
                LinearLayoutManager(this)
            }

        binding.rvUser.setHasFixedSize(true)
        binding.rvUser.adapter = adapter

        adapter.setOnItemClickCallback { data -> selectedUser(data) }
    }

    private fun selectedUser(user: FavoriteEntity) {
        val detail = Intent(this, DetailUserActivity::class.java)
        detail.putExtra(DetailUserActivity.EXTRA_USER, user.login)
        detail.putExtra(DetailUserActivity.EXTRA_PREVIOUS_ACTIVITY, FavoriteActivity::class.java.name)
        startActivity(detail)
    }

    private fun obtainViewModel(activity: AppCompatActivity): FavoriteViewModel {
        val factory = FavoriteViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(FavoriteViewModel::class.java)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}