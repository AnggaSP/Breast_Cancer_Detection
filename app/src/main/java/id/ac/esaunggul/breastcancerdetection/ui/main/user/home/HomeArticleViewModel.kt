package id.ac.esaunggul.breastcancerdetection.ui.main.user.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ac.esaunggul.breastcancerdetection.data.model.ArticleModel
import id.ac.esaunggul.breastcancerdetection.data.repo.Repo
import id.ac.esaunggul.breastcancerdetection.util.state.ResourceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeArticleViewModel
@ViewModelInject
constructor(
    private val repo: Repo
) : ViewModel() {

    private val _articles = MutableLiveData<ResourceState<List<ArticleModel>>>()
    val articles: LiveData<ResourceState<List<ArticleModel>>> = _articles

    private val _shouldHold = MutableLiveData<Boolean>(false)
    val shouldHold: LiveData<Boolean> = _shouldHold

    init {
        getArticles()
    }

    private fun getArticles() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.fetchArticles().collect { data -> _articles.postValue(data) }
        }
    }

    fun hold() {
        _shouldHold.value = true
    }

    fun releaseHold() {
        _shouldHold.value = false
    }
}