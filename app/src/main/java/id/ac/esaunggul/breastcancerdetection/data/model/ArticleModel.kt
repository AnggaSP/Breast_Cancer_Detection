package id.ac.esaunggul.breastcancerdetection.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import java.util.Date

data class ArticleModel(
    @DocumentId
    var uid: String = "",
    var author: String = "",
    var content: String = "",
    @get:PropertyName("date_published")
    @set:PropertyName("date_published")
    var datePublished: Date = Date(),
    @get:PropertyName("image_url")
    @set:PropertyName("image_url")
    var imageUrl: String = "",
    @get:PropertyName("image_author_url")
    @set:PropertyName("image_author_url")
    var imageAuthorUrl: String = "",
    var title: String = ""
)