package com.wanzz.githubuserapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wanzz.githubuserapp.database.FavoriteEntity
import com.wanzz.githubuserapp.databinding.UserItemBinding

class FavoriteAdapter : RecyclerView.Adapter<FavoriteAdapter.ListViewHolder>() {

    private val listFavorite = ArrayList<FavoriteEntity>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setData(data: List<FavoriteEntity>) {
        val diffCallback = DiffUtilCallback(listFavorite, data)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listFavorite.clear()
        listFavorite.addAll(data)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val fav = listFavorite[position]
        holder.bind(fav)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(
                listFavorite[position]
            )
        }
    }

    override fun getItemCount(): Int {
        return listFavorite.size
    }

    class ListViewHolder(private val _binding: UserItemBinding) :
        RecyclerView.ViewHolder(_binding.root) {
        fun bind(fav: FavoriteEntity) {
            _binding.tvUsername.text = fav.login

            Glide.with(itemView.context)
                .load(fav.avatarUrl)
                .skipMemoryCache(true)
                .into(_binding.imgAvatar)
        }
    }

    fun interface OnItemClickCallback {
        fun onItemClicked(selected: FavoriteEntity)
    }

    class DiffUtilCallback(
        private val oldList: List<FavoriteEntity>,
        private val newList: List<FavoriteEntity>
    ) :
        DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return oldItem.javaClass == newItem.javaClass
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return oldItem.hashCode() == newItem.hashCode()
        }

        @Override
        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return super.getChangePayload(oldItemPosition, newItemPosition)
        }
    }
}