package com.wanzz.githubuserapp.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.wanzz.githubuserapp.adapter.FollowAdapter
import com.wanzz.githubuserapp.databinding.FragmentFollowingBinding
import com.wanzz.githubuserapp.viewmodel.FollowingViewModel

class FollowingFragment : Fragment() {

    private val viewModel: FollowingViewModel by viewModels()
    private val adapter = FollowAdapter()

    private lateinit var binding: FragmentFollowingBinding
    private val _binding get() = binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowingBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showViewModel()
        showRecyclerView()
        viewModel.getIsLoading.observe(viewLifecycleOwner, this::showLoading)
    }

    private fun showViewModel() {
        viewModel.following(requireContext(), DetailUserActivity.username)
        viewModel.getFollowing.observe(viewLifecycleOwner) { following ->
            if (following.size != 0) {
                binding.rvFollowing.visibility = View.VISIBLE
                binding.tvNotfound.visibility = View.GONE
                adapter.setData(following)
            } else {
                binding.rvFollowing.visibility = View.GONE
                binding.tvNotfound.visibility = View.VISIBLE
            }
        }
    }

    private fun showRecyclerView() {
        with(binding.rvFollowing) {
            layoutManager = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager(context, 2)
            } else {
                LinearLayoutManager(context)
            }
            setHasFixedSize(true)
            adapter = this@FollowingFragment.adapter
        }
    }

    private fun showLoading(isLoading: Boolean) { binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE }

    override fun onResume() {
        super.onResume()
        viewModel.following(requireContext(), DetailUserActivity.username)
    }
}