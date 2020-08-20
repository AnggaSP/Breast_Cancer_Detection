package id.ac.esaunggul.breastcancerdetection.data.model

data class DetectionResponseModel(
    val message: String,
    val result: DetectionResultModel,
    val status: Int
)