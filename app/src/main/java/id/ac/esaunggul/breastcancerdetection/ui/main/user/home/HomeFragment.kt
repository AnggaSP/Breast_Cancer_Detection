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

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.platform.Hold
import com.google.android.material.transition.platform.MaterialFadeThrough
import id.ac.esaunggul.breastcancerdetection.BreastCancerDetection
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentHomeBinding
import id.ac.esaunggul.breastcancerdetection.util.binding.ClickListener
import id.ac.esaunggul.breastcancerdetection.util.factory.MainViewModelFactory
import id.ac.esaunggul.breastcancerdetection.util.state.ResourceState
import javax.inject.Inject

class HomeFragment : Fragment(), ClickListener {

    companion object {
        private const val TAG = "Home"
    }

    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory

    private val homeArticleViewModel: HomeArticleViewModel by viewModels {
        mainViewModelFactory
    }

    override fun onAttach(context: Context) {
        (requireActivity().application as BreastCancerDetection).mainComponent().inject(this)

        super.onAttach(context)
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

        binding.lifecycleOwner = this

        val adapter = HomeArticleCardAdapter(this)

        binding.homeRecyclerView.setAdapter(adapter)
        binding.homeRecyclerView.setLayoutManager(LinearLayoutManager(requireActivity()))
        binding.homeRecyclerView.addVeiledItems(2)

        homeArticleViewModel.articles.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ResourceState.Error -> Log.d(TAG, "An error occurred: ${it.code}")
                is ResourceState.Success -> {
                    Log.d(TAG, "Successfully fetched the resources")
                    postponeEnterTransition()
                    adapter.submitList(it.data)
                    binding.homeRecyclerView.unVeil()
                }
                is ResourceState.Loading -> {
                    Log.d(TAG, "Fetching the resources")
                }
            }
        })

        homeArticleViewModel.shouldHold.observe(viewLifecycleOwner, Observer {
            exitTransition = when (it) {
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