package id.ac.esaunggul.breastcancerdetection.data.model

import java.util.Date

data class DiagnosisModel(
    var uid: String? = null,
    var name: String? = null,
    var address: String? = null,
    var history: String? = null,
    var date: Date? = null
)