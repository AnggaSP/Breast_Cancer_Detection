package id.ac.esaunggul.breastcancerdetection.data.model

import java.util.Date

data class ConsultationModel(
    var uid: String? = null,
    var name: String? = null,
    var address: String? = null,
    var history: String? = null,
    var date: Date? = null,
    var concern: String? = null
)