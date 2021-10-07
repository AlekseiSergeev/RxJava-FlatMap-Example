package com.example.rxjava_flatmap_example.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rxjava_flatmap_example.databinding.LayoutPostListItemBinding
import com.example.rxjava_flatmap_example.models.Post
import java.util.*


class RecyclerAdapter : ListAdapter<Post, RecyclerAdapter.MyViewHolder>(PostDiffCallback()) {
    private var posts: List<Post> = ArrayList()
    private var listener: OnPostClickListener? = null
    private lateinit var binding: LayoutPostListItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = LayoutPostListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun setPosts(posts: List<Post>) {
        this.posts = posts
        submitList(posts)
    }

    fun updatePost(post: Post) {
        val array = toArray(posts)
        array[posts.indexOf(post)] = post
        notifyItemChanged(posts.indexOf(post))
    }

    private fun toArray(posts: List<Post>): MutableList<Post> {
        return posts.toMutableList()
    }

    fun getPosts(): List<Post> {
        return posts
    }

    interface OnPostClickListener {
        fun onPostClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnPostClickListener) {
        this.listener = listener
    }


    inner class MyViewHolder(binding: LayoutPostListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val title = binding.title

        init {
            title.setOnClickListener {
                listener?.onPostClick(bindingAdapterPosition)
            }
        }

        fun bind(post: Post) {
            title.text = post.title
        }

    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
        oldItem.title == newItem.title
}
