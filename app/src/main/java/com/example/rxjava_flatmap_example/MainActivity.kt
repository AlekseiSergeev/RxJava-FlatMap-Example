package com.example.rxjava_flatmap_example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rxjava_flatmap_example.databinding.ActivityMainBinding
import com.example.rxjava_flatmap_example.models.Comment
import com.example.rxjava_flatmap_example.models.Post
import com.example.rxjava_flatmap_example.request.ServiceGenerator
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.functions.Function
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val postAdapter = RecyclerAdapter()
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        initRecycler()
        getPostObservable()
            .subscribeOn(Schedulers.io())
            .flatMap(object : Function<Post, ObservableSource<Post>> {
                override fun apply(t: Post): ObservableSource<Post> {
                    return getCommentsObservable(t)
                }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Post> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onNext(t: Post) {
                    updatePost(t)
                }

                override fun onError(e: Throwable) {
                    Timber.d("onError: $e")
                }

                override fun onComplete() {
                    Timber.d("onComplete: called")
                }
            })
    }

    private fun updatePost(post: Post) {
        postAdapter.updatePost(post)
    }

    private fun getCommentsObservable(post: Post): Observable<Post> {
        val serviceGenerator = ServiceGenerator()
        return serviceGenerator.getRequestApi()
            .getComments(post.id!!)
            .map(object : Function<List<Comment>, Post> {
                override fun apply(t: List<Comment>): Post {
                    val delay = (Random().nextInt(5) + 1) * 1000
                    Thread.sleep(delay.toLong())
                    Timber.d("apply: sleeping Thread ${Thread.currentThread().name} for $delay ms. Comments= ${t.size}")
                    post.comments = t

                    return post
                }
            })
            .subscribeOn(Schedulers.io())
    }

    private fun getPostObservable(): Observable<Post> {
        val serviceGenerator = ServiceGenerator()
        return serviceGenerator.getRequestApi()
            .getPosts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(object : Function<List<Post>, ObservableSource<Post>> {
                override fun apply(t: List<Post>): ObservableSource<Post> {
                    postAdapter.setPosts(t)
                    return Observable.fromIterable(t)
                        .subscribeOn(Schedulers.io())
                }
            })
    }

    private fun initRecycler() {
        binding.recyclerView.apply {
            adapter = postAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}
