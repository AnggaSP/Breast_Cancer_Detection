package id.ac.esaunggul.breastcancerdetection.ui.main.admin.diagnosis.upload

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentDiagnosisAdminUploadBinding
import id.ac.esaunggul.breastcancerdetection.ui.main.admin.diagnosis.DiagnosisViewModel

@AndroidEntryPoint
class DiagnosisUploadFragment : Fragment() {

    private val diagnosisUploadViewModel: DiagnosisUploadViewModel by navGraphViewModels(
        R.id.fragment_navigation_diagnosis_upload_admin
    ) {
        defaultViewModelProviderFactory
    }
    private val args: DiagnosisUploadFragmentArgs by navArgs()
    private lateinit var binding: FragmentDiagnosisAdminUploadBinding

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

        diagnosisUploadViewModel.setPosition(args.position)

        binding = FragmentDiagnosisAdminUploadBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.diagnosisViewModel = diagnosisViewModel
        binding.diagnosisUploadViewModel = diagnosisUploadViewModel

        binding.diagnosisUploadMammography.setOnClickListener {
            openImageChooser()
        }

        binding.diagnosisUploadAddImageText.setOnClickListener {
            openImageChooser()
        }

        val viewType: Array<String> = resources.getStringArray(R.array.diagnosis_view)
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), R.layout.dropdown_menu_diagnosis_view, viewType)

        binding.diagnosisLayoutViewSpinner.setAdapter(adapter)

        binding.diagnosisUploadConfirmButton.setOnClickListener {
            when {
                diagnosisUploadViewModel.selectedImage.value == null -> {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.warn_image_not_selected),
                        Toast.LENGTH_LONG
                    ).show()
                }
                binding.diagnosisLayoutViewSpinner.text.toString().isEmpty() -> {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.warn_view_not_selected),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    diagnosisUploadViewModel.setSelectedView(binding.diagnosisLayoutViewSpinner.text.toString())
                    findNavController().navigate(DiagnosisUploadFragmentDirections.actionUploadToLoading())
                }
            }
        }

        return binding.root
    }

    private fun openImageChooser() {
        Intent(Intent.ACTION_PICK).also { intent ->
            val mimeTypes = arrayOf("image/png")
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(intent, REQUEST_CODE_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK) {
            diagnosisUploadViewModel.setSelectedImage(data?.data)
            binding.diagnosisUploadMammography.setImageURI(data?.data)
        }
    }

    companion object {
        private const val REQUEST_CODE_IMAGE = 200
    }
}