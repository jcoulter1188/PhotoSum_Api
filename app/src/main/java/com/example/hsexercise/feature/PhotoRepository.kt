package com.example.hsexercise.feature

import android.app.Application
import com.example.hsexercise.common.DatabaseProvider
import com.example.hsexercise.common.NetworkProvider
import com.example.hsexercise.feature.database.FeatureModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class PhotoRepository(app: Application) {
    private lateinit var subscription: Disposable
    private val retrofit = NetworkProvider.provideRestClient()
    private val webService: WebService =
        retrofit.createRetrofitAdapter().create(WebService::class.java)
    private val featureTableDao =
        DatabaseProvider.provideRoomDatabase(app).featureTableDao()


    fun getRemotePhotos(): Observable<List<FeatureModel>> {
        return webService.getPhotosList()
    }

    fun getLocalPhotos(): Observable<List<FeatureModel>> {
        return featureTableDao.getAll().toObservable()
    }

    fun saveLocalPhotos(featureModel: List<FeatureModel>) {
        featureTableDao.insertAll(featureModel)
    }

    fun loadPhotos() {
        subscription = Observable.fromCallable { getLocalPhotos() }
            .concatMap {
                if (it == it.isEmpty)
                    webService.getPhotosList().concatMap {
                            featureModel ->
                        featureTableDao.insertAll(featureModel)
                        Observable.just(featureModel)
                    }

                else
                    Observable.just(it)
            }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

    }
}
