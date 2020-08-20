package id.ac.esaunggul.breastcancerdetection.ui.main.user.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentHomeArticleDetailBinding

@AndroidEntryPoint
class HomeArticleDetailFragment : Fragment() {

    private val homeArticleViewModel: HomeArticleViewModel by navGraphViewModels(R.id.fragment_navigation_home) {
        defaultViewModelProviderFactory
    }

    private val args: HomeArticleDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialFadeThrough()

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            containerColor = Color.WHITE
            drawingViewId = R.id.common_nav_host_fragment
        }

        sharedElementReturnTransition = MaterialContainerTransform().apply {
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

        binding.articleDetailParentLayout.transitionName =
            "shared_article_container_transition_${args.position}"

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        homeArticleViewModel.releaseHold()
    }
}