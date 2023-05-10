package com.setyo.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.setyo.storyapp.R
import com.setyo.storyapp.api.ListStoryItem
import com.setyo.storyapp.databinding.ItemRowStoryBinding
import com.setyo.storyapp.ui.detail.DetailActivity
import com.setyo.storyapp.ui.detail.DetailActivity.Companion.EXTRA_DATA

class ListStoryAdapter: PagingDataAdapter<ListStoryItem, ListStoryAdapter.ListViewHolder>(ITEM_CALLBACK) {

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
       val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    class ListViewHolder(private val binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.apply {
                textViewItemName.text = story.name
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .fitCenter()
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_loading))
                    .into(imageViewPhoto)

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    intent.putExtra(EXTRA_DATA, story)

                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(imageViewPhoto, "story"),
                            Pair(textViewItemName, "name"),
                        )
                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    companion object {
        private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
               return oldItem.name == newItem.name && oldItem.description == newItem.description &&
                       oldItem.photoUrl == newItem.photoUrl
            }
        }
    }
}