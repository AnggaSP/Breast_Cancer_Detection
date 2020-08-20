package id.ac.esaunggul.breastcancerdetection.data.model

import android.net.Uri
import com.google.firebase.firestore.PropertyName

data class UserModel(
    var uid: String = "",
    var name: String? = null,
    var email: String? = null,
    @get:PropertyName("photo_url")
    @set:PropertyName("photo_url")
    var photoUrl: Uri? = null
)