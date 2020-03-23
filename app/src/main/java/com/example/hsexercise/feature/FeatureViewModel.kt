package com.example.hsexercise.feature

import android.app.Application
import androidx.lifecycle.*
import com.example.hsexercise.feature.database.FeatureModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers


class FeatureViewModel : ViewModel() {

    private lateinit var repository: PhotoRepository

    private val photoResults = MutableLiveData<List<FeatureModel>>()
    private val loadingVisibility: MutableLiveData<Int> = MutableLiveData()
    private val errorMessage: MutableLiveData<String> = MutableLiveData()
    private val disposables: CompositeDisposable = CompositeDisposable()

    class Factory :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = FeatureViewModel() as T
    }

    fun getPhotosResults(): LiveData<List<FeatureModel>>{
        return photoResults
    }

    fun getErrorMessage(): LiveData<String>{
        return errorMessage
    }

    fun isLoading(): LiveData<Int>{
        return loadingVisibility
    }

    fun getPhotos() {
        disposables.add(repository.getLocalPhotos()
            .concatMap {
                repository.getRemotePhotos().concatMap {
                        featureModel ->
                    repository.saveLocalPhotos(featureModel)
                    Observable.just(featureModel)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                photoResults.postValue(it)
                loadingVisibility.postValue(1)
            }
            .subscribe(
                { loadingVisibility.postValue(0) },
                { errorMessage.postValue(it.message) }
            ))
    }

    override fun onCleared() {
        disposables.clear()
    }

    fun setRepo(application: Application){
        repository = PhotoRepository(application)
    }
}

