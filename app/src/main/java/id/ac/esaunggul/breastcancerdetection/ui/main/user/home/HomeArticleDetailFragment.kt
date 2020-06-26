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
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.material.transition.platform.MaterialContainerTransform
import id.ac.esaunggul.breastcancerdetection.BreastCancerDetection
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentHomeArticleDetailBinding
import id.ac.esaunggul.breastcancerdetection.util.factory.MainViewModelFactory
import javax.inject.Inject

class HomeArticleDetailFragment : Fragment() {

    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory

    private val homeArticleViewModel: HomeArticleViewModel by navGraphViewModels(R.id.navigation_main) {
        mainViewModelFactory
    }

    private val args: HomeArticleDetailFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        (requireActivity().application as BreastCancerDetection).mainComponent().inject(this)

        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = 300L
            containerColor = Color.WHITE
            drawingViewId = R.id.common_nav_host_fragment
        }

        sharedElementReturnTransition = MaterialContainerTransform().apply {
            duration = 300L
            containerColor = Color.WHITE
            drawingViewId = R.id.common_nav_host_fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHomeArticleDetailBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.articleViewModel = homeArticleViewModel
        binding.position = args.position

        postponeEnterTransition()
        binding.articleDetailParentLayout.transitionName =
            "shared_article_container_transition_${args.position}"
        startPostponedEnterTransition()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        homeArticleViewModel.releaseHold()
    }
}