package com.example.rxjava_flatmap_example

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rxjava_flatmap_example.databinding.LayoutPostListItemBinding
import com.example.rxjava_flatmap_example.models.Post
import java.util.*


class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {
    private var posts: List<Post> = ArrayList()
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
        notifyDataSetChanged()
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

    inner class MyViewHolder(binding: LayoutPostListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val title = binding.title
        private val numComments = binding.numComments
        private val progressBar = binding.progressBar
        fun bind(post: Post) {
            title.text = post.title
            if (post.comments == null) {
                showProgressBar(true)
                numComments.text = ""
            } else {
                showProgressBar(false)
                numComments.text = ((post.comments)?.size).toString()
            }
        }

        private fun showProgressBar(showProgressBar: Boolean) {
            if (showProgressBar) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }
}
