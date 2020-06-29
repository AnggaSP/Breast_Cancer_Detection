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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentHomeBinding
import id.ac.esaunggul.breastcancerdetection.util.binding.ClickListener
import id.ac.esaunggul.breastcancerdetection.util.state.ResourceState
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment(), ClickListener {

    private val homeArticleViewModel: HomeArticleViewModel by navGraphViewModels(R.id.fragment_navigation_home) {
        defaultViewModelProviderFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = HomeArticleCardAdapter(this)
        binding.homeRecyclerView.setAdapter(adapter)
        binding.homeRecyclerView.setLayoutManager(LinearLayoutManager(requireActivity()))
        binding.homeRecyclerView.addVeiledItems(2)

        homeArticleViewModel.articles.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ResourceState.Error -> Timber.e("An error occurred: ${state.code}")
                is ResourceState.Success -> {
                    Timber.d("Successfully fetched the resources")
                    postponeEnterTransition()
                    adapter.submitList(state.data)
                    binding.homeRecyclerView.unVeil()
                }
                is ResourceState.Loading -> {
                    Timber.d("Fetching the resources")
                }
            }
        })

        homeArticleViewModel.shouldHold.observe(viewLifecycleOwner, Observer { hold ->
            exitTransition = when (hold) {
                true -> Hold()
                else -> MaterialFadeThrough()
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onClick(position: Int, view: View?) {
        homeArticleViewModel.hold()
        if (view != null) {
            val extraParam = FragmentNavigatorExtras(
                view to view.transitionName
            )
            val direction = HomeFragmentDirections.actionHomeToDetail(position)
            findNavController().navigate(direction, extraParam)
        } else {
            findNavController().navigate(HomeFragmentDirections.actionHomeToDetail(position))
        }
    }
}