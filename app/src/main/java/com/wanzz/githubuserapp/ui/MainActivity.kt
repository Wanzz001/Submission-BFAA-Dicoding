package com.wanzz.githubuserapp.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.wanzz.githubuserapp.R
import com.wanzz.githubuserapp.adapter.UserAdapter
import com.wanzz.githubuserapp.databinding.ActivityMainBinding
import com.wanzz.githubuserapp.response.ItemsItem
import com.wanzz.githubuserapp.settings.SettingPreference
import com.wanzz.githubuserapp.settings.SettingViewModelFactory
import com.wanzz.githubuserapp.viewmodel.SearchUserViewModel
import com.wanzz.githubuserapp.viewmodel.SettingViewModel

class MainActivity : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
    private lateinit var viewModelSetting: SettingViewModel
    private lateinit var binding: ActivityMainBinding
    private val viewModel: SearchUserViewModel by viewModels()
    private val adapter = UserAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            viewModel.restoreState()
        } else {
            viewModel.searchUser(this,"a")
        }


        darkModeCheck()
        viewModel.getIsLoading.observe(this, this::showLoading)
        showViewModel()
        showRecyclerView()
        toFavoriteActivity()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.saveState()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        viewModel.restoreState()
    }

    private fun toFavoriteActivity() {
        binding.fabFavorite.setOnClickListener {
            val fav = Intent(this, FavoriteActivity::class.java)
            startActivity(fav)
        }
    }

    private fun darkModeCheck() {
        val pref = SettingPreference.getInstance(dataStore)
        viewModelSetting =
            ViewModelProvider(this, SettingViewModelFactory(pref))[SettingViewModel::class.java]

        viewModelSetting.getThemeSettings().observe(this@MainActivity) { isDarkModeActive ->
            if (isDarkModeActive) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.setting) {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showViewModel() {
        viewModel.getSearchList.observe(this) { searchList ->
            if (searchList != null) {
                if (searchList.size != 0) {
                    binding.rvUser.visibility = View.VISIBLE
                    binding.tvNotfound.visibility = View.GONE
                    if (searchList != null) {
                        adapter.setData(searchList)
                    }
                } else {
                    binding.rvUser.visibility = View.GONE
                    binding.tvNotfound.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showRecyclerView() {
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

    private fun selectedUser(user: ItemsItem) {
        val detail = Intent(this, DetailUserActivity::class.java)
        detail.putExtra(DetailUserActivity.EXTRA_USER, user.login)
        detail.putExtra(DetailUserActivity.EXTRA_PREVIOUS_ACTIVITY, MainActivity::class.java.name)
        startActivity(detail)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        val close = menu.findItem(R.id.search)

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = "Search users"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.searchUser(this@MainActivity, query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        close.icon?.setVisible(false, false)

        return true
    }

    private fun showLoading(isLoading: Boolean) { binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE }
}