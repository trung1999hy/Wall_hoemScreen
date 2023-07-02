package com.example.backgroundimage.network

import com.example.backgroundimage.model.Category
import com.example.backgroundimage.model.Gif
import com.example.backgroundimage.model.Image
import com.example.backgroundimage.model.WallResponse
import retrofit2.Call
import retrofit2.http.GET

interface InterfaceAPI {
    @GET("/wall/categories")
    fun getCategories() : Call<WallResponse<Category>>

    @GET("/wall/gifs")
    fun getGifs() : Call<WallResponse<Gif>>

    @GET("/wall/images")
    fun getImages() : Call<WallResponse<Image>>
}