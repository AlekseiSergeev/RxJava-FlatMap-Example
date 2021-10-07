package com.example.rxjava_flatmap_example.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rxjava_flatmap_example.BuildConfig
import com.example.rxjava_flatmap_example.R
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
import io.reactivex.rxjava3.functions.Predicate
import io.reactivex.rxjava3.subjects.PublishSubject
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

const val PERIOD: Long = 100

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val postAdapter = RecyclerAdapter()
    private val disposables = CompositeDisposable()
    private val publishSubject: PublishSubject<Post> = PublishSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        initRecycler()
        retrievePosts()
    }

    private fun initSwitchMapDemo() {
        publishSubject
            .switchMap(object : Function<Post, ObservableSource<Post>> {
                override fun apply(post: Post): ObservableSource<Post> {
                    return Observable
                        .interval(PERIOD, TimeUnit.MILLISECONDS)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .takeWhile(object : Predicate<Long> {
                            override fun test(t: Long): Boolean {
                                binding.progressBar.max = (3000 - PERIOD).toInt()
                                binding.progressBar.progress =
                                    Integer.parseInt((t * PERIOD + PERIOD).toString())
                                return t <= (3000 / PERIOD)
                            }
                        })
                        .filter(object : Predicate<Long> {
                            override fun test(t: Long): Boolean {
                                return t >= (3000 / PERIOD)
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .flatMap(object : Function<Long, ObservableSource<Post>> {
                            override fun apply(t: Long): ObservableSource<Post> {
                                return ServiceGenerator().getRequestApi().getPost(post.id!!)
                            }
                        })
                }
            })
            .subscribe(object : Observer<Post> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onNext(t: Post) {
                    navViewPostActivity(t)
                }

                override fun onError(e: Throwable) {
                    Timber.e("onError: $e")
                }

                override fun onComplete() {
                }
            })
    }

    private fun retrievePosts() {
        ServiceGenerator()
            .getRequestApi()
            .getPosts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<Post>> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onNext(t: List<Post>) {
                    postAdapter.setPosts(t)
                    getPostTitle()
                }

                override fun onError(e: Throwable) {
                    Timber.e("onError: $e")
                }

                override fun onComplete() {
                }
            })

    }

    private fun navViewPostActivity(post: Post) {
        val intent = Intent(this, ViewPostActivity::class.java)
        intent.putExtra("post", post.title)
        startActivity(intent)
    }

    fun getPostTitle() {
        postAdapter.setOnItemClickListener(object : RecyclerAdapter.OnPostClickListener {
            override fun onPostClick(position: Int) {
                publishSubject.onNext(postAdapter.getPosts()[position])
            }
        })
    }

    override fun onResume() {
        super.onResume()
        binding.progressBar.progress = 0
        initSwitchMapDemo()
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
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
