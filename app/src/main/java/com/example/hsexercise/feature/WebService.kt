package com.example.hsexercise.feature

import com.example.hsexercise.feature.database.FeatureModel
import io.reactivex.Observable
import retrofit2.http.GET

interface WebService {
    @GET("https://picsum.photos/v2/list")
    fun getPhotosList(): Observable<List<FeatureModel>>
}