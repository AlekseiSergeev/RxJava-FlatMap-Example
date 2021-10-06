package com.example.rxjava_flatmap_example.request

import com.example.rxjava_flatmap_example.models.Comment
import com.example.rxjava_flatmap_example.models.Post
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface RequestApi {

    @GET("posts")
    fun getPosts(): Observable<List<Post>>

    @GET("posts/{id}/comments")
    fun getComments(@Path("id") id: Int): Observable<List<Comment>>
}