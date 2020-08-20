package id.ac.esaunggul.breastcancerdetection.ui.main.admin.diagnosis.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentDiagnosisAdminBinding
import id.ac.esaunggul.breastcancerdetection.ui.main.admin.diagnosis.DiagnosisViewModel
import id.ac.esaunggul.breastcancerdetection.util.binding.ClickListener
import id.ac.esaunggul.breastcancerdetection.util.state.ResourceState
import timber.log.Timber

@AndroidEntryPoint
class DiagnosisFragment : Fragment(), ClickListener {

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
        val diagnosisViewModel: DiagnosisViewModel by navGraphViewModels(
            R.id.fragment_navigation_diagnosis_admin
        ) {
            defaultViewModelProviderFactory
        }

        val binding = FragmentDiagnosisAdminBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner

        val adapter =
            DiagnosisCardAdapter(
                this
            )
        binding.diagnosisAdminRecyclerView.setAdapter(adapter)
        binding.diagnosisAdminRecyclerView.setLayoutManager(LinearLayoutManager(requireContext()))
        binding.diagnosisAdminRecyclerView.addVeiledItems(4)

        diagnosisViewModel.diagnosis.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ResourceState.Error -> Timber.e("An error occurred: ${state.code}")
                is ResourceState.Success -> {
                    Timber.d("Successfully fetched the resources")
                    adapter.submitList(state.data)
                    binding.diagnosisAdminRecyclerView.unVeil()
                }
                is ResourceState.Loading -> {
                    Timber.d("Fetching the resources")
                }
            }
        })

        return binding.root
    }

    override fun onClick(position: Int, view: View?) {
        findNavController().navigate(
            DiagnosisFragmentDirections.actionDiagnosisToDetailAdmin(
                position
            )
        )
    }
}