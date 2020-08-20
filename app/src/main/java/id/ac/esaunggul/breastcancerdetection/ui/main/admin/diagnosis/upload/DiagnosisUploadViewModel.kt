package id.ac.esaunggul.breastcancerdetection.ui.main.admin.diagnosis.upload

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.data.api.DetectionApi
import id.ac.esaunggul.breastcancerdetection.data.model.DetectionResponseModel
import id.ac.esaunggul.breastcancerdetection.util.dispatcher.NavigationDispatcher
import id.ac.esaunggul.breastcancerdetection.util.dispatcher.ToastDispatcher
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.text.DecimalFormat

class DiagnosisUploadViewModel
@ViewModelInject
constructor(
    private val detectionApi: DetectionApi,
    private val navigationDispatcher: NavigationDispatcher,
    private val toastDispatcher: ToastDispatcher
) : ViewModel() {

    private val _position = MutableLiveData<Int>()
    val position: LiveData<Int> = _position

    private val _selectedImage = MutableLiveData<Uri>()
    val selectedImage: LiveData<Uri> = _selectedImage

    private val _selectedView = MutableLiveData<String>()
    val selectedView: LiveData<String> = _selectedView

    private val _benign = MutableLiveData<String>()
    val benign: LiveData<String> = _benign

    private val _malignant = MutableLiveData<String>()
    val malignant: LiveData<String> = _malignant

    fun uploadAndInfer(file: File) {
        Timber.d("Inferring mammography, this may take 10 minutes or more.")
        val requestFile: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        detectionApi.inferMammography(
            MultipartBody.Part.createFormData("image", file.name, requestFile),
            RequestBody.create(MediaType.parse("multipart/form-data"), selectedView.value!!)
        ).enqueue(object: Callback<DetectionResponseModel>{
            override fun onFailure(call: Call<DetectionResponseModel>, t: Throwable) {
                Timber.d("An error has occurred: ${t.message}")
            }

            override fun onResponse(
                call: Call<DetectionResponseModel>,
                response: Response<DetectionResponseModel>
            ) {
                if (response.code() == 400) {
                    Timber.d("Failed to infer: ${response.body()?.message}")
                    navigationDispatcher.emit { navController ->
                        navController.navigateUp()
                        toastDispatcher.emit(R.string.warn_image_not_supported)
                    }
                } else if (response.code() == 200) {
                    val percentFormat = DecimalFormat("#.#%")

                    _benign.postValue(percentFormat.format(response.body()?.result?.benign))
                    _malignant.postValue(percentFormat.format(response.body()?.result?.malignant))

                    navigationDispatcher.emit { navController ->
                        navController.navigate(DiagnosisLoadingFragmentDirections.actionLoadingToResult())
                    }
                }
            }
        })
    }

    fun setPosition(position: Int) {
        _position.value = position
    }

    fun setSelectedImage(image: Uri?) {
        _selectedImage.value = image
    }

    fun setSelectedView(view: String) {
        _selectedView.value = view
    }
}