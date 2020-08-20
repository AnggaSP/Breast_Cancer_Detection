package id.ac.esaunggul.breastcancerdetection.data.api

import id.ac.esaunggul.breastcancerdetection.data.model.DetectionResponseModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface DetectionApi {

    @Multipart
    @POST("classify")
    fun inferMammography(
        @Part image: MultipartBody.Part,
        @Part("view") view: RequestBody
    ) : Call<DetectionResponseModel>
}