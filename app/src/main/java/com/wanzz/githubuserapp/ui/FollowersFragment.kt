package com.wanzz.githubuserapp.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.wanzz.githubuserapp.adapter.FollowAdapter
import com.wanzz.githubuserapp.databinding.FragmentFollowersBinding
import com.wanzz.githubuserapp.viewmodel.FollowersViewModel


class FollowersFragment : Fragment() {

    private val viewModel: FollowersViewModel by viewModels()
    private val adapter = FollowAdapter()

    private lateinit var binding: FragmentFollowersBinding
    private val _binding get() = binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowersBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showViewModel()
        showRecyclerView()
        viewModel.getIsLoading.observe(viewLifecycleOwner, this::showLoading)
    }

    private fun showViewModel() {
        viewModel.followers(requireContext(), DetailUserActivity.username)
        viewModel.getFollowers.observe(viewLifecycleOwner) { followers ->
            if (followers.size != 0) {
                binding.rvFollowers.visibility = View.VISIBLE
                binding.tvNotfound.visibility = View.GONE
                adapter.setData(followers)
            } else {
                binding.rvFollowers.visibility = View.GONE
                binding.tvNotfound.visibility = View.VISIBLE
            }
        }
    }

    private fun showRecyclerView() {
        with(binding.rvFollowers) {
            layoutManager = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager(context, 2)
            } else {
                LinearLayoutManager(context)
            }
            setHasFixedSize(true)
            adapter = this@FollowersFragment.adapter
        }
    }


    private fun showLoading(isLoading: Boolean) { binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE }

    override fun onResume() {
        super.onResume()
        viewModel.followers(requireContext(), DetailUserActivity.username)
    }
}