package com.example.hsexercise.feature.database

import androidx.room.*
import com.google.gson.annotations.SerializedName
import io.reactivex.Maybe

@Dao
interface FeatureTableDao {
    @Query("SELECT * FROM feature")
    fun getAll(): Maybe<List<FeatureModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(models: List<FeatureModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(featureModel: FeatureModel)
}

@Entity(tableName = "feature")
data class FeatureModel(
    @PrimaryKey
    @SerializedName("id") val id : String,
    @SerializedName("author") val author : String,
    @SerializedName("width") val width : Int,
    @SerializedName("height") val height : Int,
    @SerializedName("download_url") val url : String
)
