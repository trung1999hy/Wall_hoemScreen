package com.example.backgroundimage.model

import com.example.backgroundimage.models.content.Categories
import com.google.gson.annotations.SerializedName


class Category : Wall {
    @SerializedName("type")
    private var type : String? = null
    @SerializedName("imageUrl")
    private var imageUrl : String? = null
    @SerializedName("title")
    private var title : String? = null

    override fun getType(): String? = type

    override fun getTitle(): String? = title

    override fun getImageUrl(): String? = imageUrl

    fun toCategory() : Categories = Categories(
        this.title ?: "", this.imageUrl ?: ""
    )
}
