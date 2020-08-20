package id.ac.esaunggul.breastcancerdetection.ui.main.admin.diagnosis.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentDiagnosisAdminLoadingBinding
import id.ac.esaunggul.breastcancerdetection.util.extensions.getFileName
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@AndroidEntryPoint
class DiagnosisLoadingFragment : Fragment() {

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
        val diagnosisUploadViewModel: DiagnosisUploadViewModel by navGraphViewModels(
            R.id.fragment_navigation_diagnosis_upload_admin
        ) {
            defaultViewModelProviderFactory
        }

        val binding = FragmentDiagnosisAdminLoadingBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.diagnosisUploadViewModel = diagnosisUploadViewModel

        val fileDescriptor = requireActivity().contentResolver.openFileDescriptor(
            diagnosisUploadViewModel.selectedImage.value!!,
            "r",
            null
        )
        val inputStream = FileInputStream(fileDescriptor?.fileDescriptor)
        val file = File(requireActivity().cacheDir, requireActivity().contentResolver.getFileName(diagnosisUploadViewModel.selectedImage.value!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        diagnosisUploadViewModel.uploadAndInfer(file)

        return binding.root
    }
}