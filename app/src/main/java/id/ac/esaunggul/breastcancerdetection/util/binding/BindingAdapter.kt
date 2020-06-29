/*
 * Copyright 2020 Angga Satya Putra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package id.ac.esaunggul.breastcancerdetection.util.binding

import android.graphics.Color
import android.net.Uri
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.textfield.TextInputLayout
import id.ac.esaunggul.breastcancerdetection.R
import java.util.Date

@BindingAdapter("error")
fun setError(viewGroup: TextInputLayout, error: MutableLiveData<Int?>) {
    if (error.value == null) {
        viewGroup.error = null
    } else {
        error.value?.let { resId ->
            viewGroup.error = viewGroup.context
                .getString((resId))
        }
    }
}

@BindingAdapter("focus")
fun setFocus(view: TextView, focus: Boolean) {
    if (focus) {
        view.requestFocus()
    } else {
        view.clearFocus()
    }
}

@BindingAdapter("imageProfile")
fun bindImageProfile(view: ImageView, url: Uri?) {
    if (url != null) {
        Glide.with(view.context)
            .load(url)
            .placeholder(R.drawable.im_placeholder)
            .error(R.drawable.im_placeholder)
            .into(view)
    } else {
        view.setImageResource(R.drawable.ic_profile_48)
    }
}

@BindingAdapter("imageUrl")
fun bindImageUrl(view: ImageView, url: String?) {
    if (url != null) {
        Glide.with(view.context)
            .load(url)
            .placeholder(R.drawable.im_placeholder)
            .error(R.drawable.im_placeholder)
            .into(view)
    } else {
        Glide.with(view.context)
            .load(R.drawable.im_placeholder)
            .into(view)
    }
}

@BindingAdapter(value = ["showProgress", "onHideText"])
fun showProgress(view: Button, show: Boolean, hideText: String) {
    if (show) {
        view.showProgress {
            textMarginPx = 0
            progressColor = Color.WHITE
        }
    } else {
        view.hideProgress(hideText)
    }
}

@BindingAdapter("textDate")
fun setTextDate(view: TextView, date: Date) {
    val formatter = android.text.format.DateFormat.getMediumDateFormat(view.context)
    view.text = formatter.format(date)
}

@BindingAdapter("textHtml")
fun setTextHTML(view: TextView, text: String) {
    view.text = HtmlCompat.fromHtml(text, FROM_HTML_MODE_COMPACT)
}