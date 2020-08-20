package id.ac.esaunggul.breastcancerdetection.util.binding

import android.view.View

interface ClickListener {

    fun onClick(
        position: Int,
        view: View? = null
    )
}