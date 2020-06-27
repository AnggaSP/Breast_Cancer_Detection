/*
 * Copyright 2020 Angga Satya Putra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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